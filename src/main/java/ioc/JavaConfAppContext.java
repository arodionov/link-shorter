package ioc;

import ioc.annotations.Benchmark;
import ioc.annotations.PostConstructBean;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class JavaConfAppContext implements BeanFactory {

    private final Map<String, Class<?>> config;
    private final Map<String, Object> beans = new HashMap<>();
    private final String PERSISTENCE_PROPERTIES_NAME = "ShorterEntityProperties";

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

                if (hasBenchmarkAnnotatedMethod(beanClass)) {
                    beans.put(beanName, createBeanBenchmarkProxy(bean, beanClass));
                } else {
                    beans.put(beanName, bean);
                }

                if (hasInitMethod(beanClass)) {
                    callInitMethod(bean, beanClass);
                }

                if (hasPostConstructMethod(beanClass)) {
                    callPostConstructMethods(bean, beanClass);
                }
                return (T) beans.get(beanName);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    private boolean hasBenchmarkAnnotatedMethod(Class<?> beanClass) {
        return Arrays.stream(beanClass.getMethods())
                .anyMatch(m -> m.isAnnotationPresent(Benchmark.class));
    }

    private boolean hasInitMethod(Class<?> beanClass) {
        return Arrays.stream(beanClass.getMethods())
                .anyMatch(m -> m.getName().equals("init"));
    }

    private boolean hasPostConstructMethod(Class<?> beanClass) {
        return Arrays.stream(beanClass.getMethods())
                .anyMatch(m -> m.isAnnotationPresent(PostConstructBean.class));
    }

    private <T> T createBeanBenchmarkProxy(Object bean, Class<?> beanClass) {
        Object proxyBean = Proxy.newProxyInstance(
                beanClass.getClassLoader(),
                bean.getClass().getInterfaces(),
                (proxy, method, args) -> {
                    Object result;
                    Method implMethod = beanClass.getMethod(method.getName(), method.getParameterTypes());
                    if (implMethod.isAnnotationPresent(Benchmark.class) ||
                            method.isAnnotationPresent(Benchmark.class)) {
                        long before = System.nanoTime();
                        result = method.invoke(bean, args);
                        long after = System.nanoTime();
                        System.out.println(method + ": " + (after - before));
                    } else {
                        result = method.invoke(bean, args);
                    }
                    return result;
                }
        );
        return (T) proxyBean;
    }

    private Object createBean(final Class<?> beanClass) throws Exception {
        if (isEntityManagerFactory(beanClass)) {
            return Persistence.createEntityManagerFactory(PERSISTENCE_PROPERTIES_NAME);
        }

        Constructor<?> constructor = Arrays.stream(beanClass.getConstructors())
                .max(Comparator.comparingInt(Constructor::getParameterCount))
                .orElseThrow(() -> new RuntimeException("Bean have no constructors"));

        return constructor.newInstance(getBeansByClasses(constructor.getParameterTypes()));
    }

    private boolean isEntityManagerFactory(final Class<?> beanClass) {
        return beanClass.equals(EntityManagerFactory.class);
    }

    private Object[] getBeansByClasses(Class<?>[] classes) {
        return Arrays.stream(classes)
                .map(Class::getName)
                .map(this::getBean)
                .toArray(Object[]::new);
    }

    private void callInitMethod(Object bean, Class<?> beanClass) {
        Arrays.stream(beanClass.getMethods())
                .filter(m -> m.getName().equals("init")).findFirst()
                .ifPresent(m -> {
                    try {
                        m.invoke(bean);
                    } catch (Exception e) {
                        // no action
                    }
                });
    }

    private void callPostConstructMethods(Object bean, Class<?> beanClass) {
        Arrays.stream(beanClass.getMethods())
                .filter(m -> m.isAnnotationPresent(PostConstructBean.class))
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
