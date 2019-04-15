package ioc;

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
                return (T) beans.get(beanName);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return null;
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
