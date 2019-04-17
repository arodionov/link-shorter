package ioc;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class JavaConfAppContext implements BeanFactory {

    private final Map<String, Class<?>> config;
    private final Map<String, Object> beans = new HashMap<>();

    public JavaConfAppContext() {
        this.config = Map.of();
    }

    public JavaConfAppContext(final Map<String, Class<?>> config) {
        this.config = config;
    }

    @Override
    public <T> T getBean(final String beanName) {
        Object bean = beans.get(beanName);
        if (bean != null)
            return (T) bean;

        Class<?> beanClass = config.get(beanName);
        if (beanClass != null) {
            try {
                bean = beanClass.getConstructor().newInstance();
                beans.put(beanName, bean);
                callInitMethod(bean, beanClass);
                callPostConstructMethod(bean, beanClass);
                return (T) beans.get(beanName);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    private void callInitMethod(Object bean, Class<?> beanClass) {
        Method[] methods = beanClass.getDeclaredMethods();
        Arrays.stream(methods).filter(m -> m.getName().equals("init")).findFirst().ifPresent(m -> {
            try {
                m.invoke(bean);
            } catch (Exception e) {
                // no action
            }
        });
    }

    private void callPostConstructMethod(Object bean, Class<?> beanClass) {
        Method[] methods = beanClass.getDeclaredMethods();
        Arrays.stream(methods).filter(m -> m.isAnnotationPresent(PostConstructBean.class))
                .forEach(m -> {
                    try {
                        m.invoke(bean);
                    } catch (Exception e) {
                        // ignore
                    }
                });
    }

    @Override
    public BeanDefinition getBeanDefinition(final String beanName) {
        return config.containsKey(beanName) ? new BeanDefinition(beanName) : null;
    }

    @Override
    public BeanDefinition[] getBeanDefinitionNames() {
        return config.keySet().stream()
                .map(BeanDefinition::new)
                .toArray(BeanDefinition[]::new);
    }
}
