package ioc;

import ioc.annotations.Benchmark;
import ioc.annotations.PostConstructBean;
import ioc.annotations.Transactional;
import shorter.util.EntityManagerProvider;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class JavaConfAppContext implements BeanFactory {

    private final Map<String, Class<?>> config;
    private final Map<String, Object> beans = new HashMap<>();
    private final String PERSISTENCE_PROPERTIES_NAME = "ShorterEntityProperties";
    private EntityManager currentEntityManager;

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
                    bean = createBeanBenchmarkProxy(bean, beanClass);
                }

                if (hasTransactionalAnnotatedMethod(beanClass)) {
                    bean = createBeanTransactionalProxy(bean, beanClass);
                }

                if (isEntityManagerProvider(beanClass)) {
                    bean = createEntityManagerProviderProxy(bean, beanClass);
                }

                beans.put(beanName, bean);

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

    private boolean isEntityManagerProvider(Class<?> beanClass) {
        return Arrays.asList(beanClass.getInterfaces()).contains(EntityManagerProvider.class);
    }

    private boolean hasTransactionalAnnotatedMethod(Class<?> beanClass) {
        return Arrays.stream(beanClass.getMethods())
                .anyMatch(m -> m.isAnnotationPresent(Transactional.class));
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

    private <T> T createEntityManagerProviderProxy(Object bean, Class<?> beanClass) {
        Object proxyBean = Proxy.newProxyInstance(
                beanClass.getClassLoader(),
                bean.getClass().getInterfaces(),
                (proxy, method, args) -> {
                    if (returnsEntityManager(beanClass, method)) {
                        if (currentEntityManager == null || !currentEntityManager.isOpen()) {
                            EntityManagerFactory entityManagerFactory = getBean(EntityManagerFactory.class.getName());
                            currentEntityManager = entityManagerFactory.createEntityManager();
                        }
                        return currentEntityManager;
                    }
                    return method.invoke(bean, args);
                }
        );
        return (T) proxyBean;
    }

    private <T> T createBeanTransactionalProxy(Object bean, Class<?> beanClass) {
        Object proxyBean = Proxy.newProxyInstance(
                beanClass.getClassLoader(),
                bean.getClass().getInterfaces(),
                (proxy, method, args) -> {
                    Object result;
                    if (hasTransactionalAnnotation(beanClass, method)) {
                        result = invokeWithTransaction(method, bean, args);
                    } else {
                        result = method.invoke(bean, args);
                    }
                    return result;
                }
        );
        return (T) proxyBean;
    }

    private Object invokeWithTransaction(Method method, Object bean, Object[] args) throws Exception {
        EntityManagerFactory entityManagerFactory = getBean(EntityManagerFactory.class.getName());
        currentEntityManager = entityManagerFactory.createEntityManager();
        currentEntityManager.getTransaction().begin();
        try {
            Object res = method.invoke(bean, args);
            currentEntityManager.getTransaction().commit();
            return res;
        } catch (Exception e) {
            currentEntityManager.getTransaction().rollback();
            throw e;
        } finally {
            currentEntityManager.close();
        }
    }

    private boolean returnsEntityManager(Class<?> beanClass, Method interfaceMethod) throws Exception {
        Method implMethod = beanClass.getMethod(interfaceMethod.getName(), interfaceMethod.getParameterTypes());
        return implMethod.getReturnType().equals(EntityManager.class) ||
                interfaceMethod.getReturnType().equals(EntityManager.class);
    }

    private boolean hasTransactionalAnnotation(Class<?> beanClass, Method interfaceMethod) throws NoSuchMethodException {
        Method implMethod = beanClass.getMethod(interfaceMethod.getName(), interfaceMethod.getParameterTypes());
        return implMethod.isAnnotationPresent(Transactional.class) || interfaceMethod.isAnnotationPresent(Transactional.class);
    }

    private <T> T createBeanBenchmarkProxy(Object bean, Class<?> beanClass) {
        Object proxyBean = Proxy.newProxyInstance(
                beanClass.getClassLoader(),
                bean.getClass().getInterfaces(),
                (proxy, method, args) -> {
                    Object result;
                    if (hasBenchmarkAnnotation(beanClass, method)) {
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

    private boolean hasBenchmarkAnnotation(Class<?> beanClass, Method interfaceMethod) throws NoSuchMethodException {
        Method implMethod = beanClass.getMethod(interfaceMethod.getName(), interfaceMethod.getParameterTypes());
        return implMethod.isAnnotationPresent(Benchmark.class) || interfaceMethod.isAnnotationPresent(Benchmark.class);
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
