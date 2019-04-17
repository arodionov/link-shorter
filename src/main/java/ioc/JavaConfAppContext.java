package ioc;

import ioc.annotations.PostConstructBean;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.stream.Collectors;

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
                bean = createBean(beanClass);
                beans.put(beanName, createBeanBenchmarkProxy(bean, beanClass));
                /*
                TODO:
                 If bean has @Benchmark annotation for any method -
                 create proxy for him, measure execution time, print it out like
                 Method name: execution time
                 */
                callInitMethod(bean, beanClass);
                callPostConstructMethod(bean, beanClass);
                return (T) beans.get(beanName);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    private <T> T createBeanBenchmarkProxy(Object bean, Class<?> beanClass) {
        Object proxyBean = Proxy.newProxyInstance(
                beanClass.getClassLoader(),
                bean.getClass().getInterfaces(),
                (proxy, method, args) -> {
                    long before = System.nanoTime();
                    Object result = method.invoke(bean, args);
                    long after = System.nanoTime();
                    System.out.println(method + ": " + (after - before));
                    return result;
                }
        );
        return (T) proxyBean;
    }

    private Object createBean(final Class<?> beanClass) throws Exception {
        Constructor<?> constructor = Arrays.stream(beanClass.getConstructors())
                .max(Comparator.comparingInt(Constructor::getParameterCount))
                .orElseThrow(() -> new RuntimeException("Bean have no constructors"));

        return constructor.newInstance(getBeansByClasses(constructor.getParameterTypes()));
    }

    private Object[] getBeansByClasses(Class<?>[] classes) {
        return Arrays.stream(classes)
                .map(Class::getName)
                .map(this::getBean)
                .toArray(Object[]::new);
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
