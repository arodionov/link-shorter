package ioc;

import ioc.annotations.Benchmark;
import ioc.annotations.PostConstructBean;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class JavaConfAppContext implements BeanFactory {

    private final Map<String, Class<?>> config;

    private final Map<String, Object> beans;

    public JavaConfAppContext(Map<String, Class<?>> config) {
        this.config = config;
        beans = new HashMap<>();
    }

    public JavaConfAppContext() {
        this.config = Collections.emptyMap();
        beans = new HashMap<>();
    }

    @Override
    public <T> T getBean(String beanName) {
        return this.beans.containsKey(beanName) ? (T) this.beans.get(beanName) : createInstance(beanName);
    }

    private <T> T createInstance(String beanName) {
        if (!this.config.containsKey(beanName)) {
            throw new RuntimeException("Cannot find originalBean definition");
        }

        T bean = getInstance(beanName);
        // after creating we add the originalBean to cache
        this.beans.put(beanName, bean);

        return bean;
    }

    private <T> T getInstance(String beanName) {
        Class<?> type = this.config.get(beanName);

        Constructor<?> constructor = type.getDeclaredConstructors()[0];

        T bean = constructor.getParameterCount() == 0 ? createBeanWithDefaultConstructor(type) : createBeanWithConstructorWithParams(type);

        callPostConstructBeanMethod(type, bean);
        callInitMethod(type, bean);
        bean = createBenchmarkProxy(type, bean);
        return bean;
    }

    private <T> T createBeanWithDefaultConstructor(Class<?> classz) {
        try {
            return (T) classz.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException();
        }
    }

    private <T> T createBeanWithConstructorWithParams(Class<?> type) {
        Constructor<?> constructor = type.getDeclaredConstructors()[0];
        Object[] dependencies = findDependencies(constructor.getParameterTypes());

        try {
            return (T) constructor.newInstance(dependencies);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Object[] findDependencies(Class<?>[] parameterTypes) {
        Object[] paramsVal = new Object[parameterTypes.length];

        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> paramType = parameterTypes[i];
            String beanName = getBeanNameFromType(paramType);
            // load dependencies from cache or create a new instance
            // recursive call
            paramsVal[i] = getBean(beanName);
        }
        return paramsVal;
    }

    private String getBeanNameFromType(Class<?> paramType) {
        String typeName = paramType.getSimpleName();
        return Character.toLowerCase(typeName.charAt(0)) + typeName.substring(1);
    }

    private <T> void callInitMethod(Class<?> type, T bean) {
        try {
            Method initMethod = type.getMethod("init");

            if (Objects.nonNull(initMethod)) {
                initMethod.invoke(bean);
            }

        } catch (NoSuchMethodException  n){
            // do nothing
        }
        catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private <T> void callPostConstructBeanMethod(Class<?> type, T bean) {
        Method[] methods = type.getMethods();
        for (Method m : methods) {
            if (m.isAnnotationPresent(PostConstructBean.class)) {
                try {
                    m.invoke(bean);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException("Cannot invoke post construct method");
                }
            }
        }
    }

    private <T> T createBenchmarkProxy(Class<?> type, T bean) {
        List<Method> benchmarkMethods = findMethodAnnotatedAsBenchmark(type);
        return benchmarkMethods.isEmpty() ? bean : getProxy(type, bean, benchmarkMethods);
    }

    private List<Method> findMethodAnnotatedAsBenchmark(Class<?> type) {
        Method[] methods = type.getMethods();
        List<Method> benchmarkMethods = new ArrayList<>();
        for (Method m : methods) {
            if (m.isAnnotationPresent(Benchmark.class)) {
                benchmarkMethods.add(m);
            }
        }
        return benchmarkMethods;
    }

    private <T> T getProxy(Class<?> type, T bean, List<Method> methods) {
        Class<?>[] interfaces = type.getInterfaces();
        return (T) Proxy.newProxyInstance(
                type.getClassLoader(),
                interfaces,
                new BenchmarkInvocationHandler(methods, bean)
        );
    }

    private static class BenchmarkInvocationHandler implements InvocationHandler {

        private final List<Method> originalBenchmarkMethods;
        private final Object originalBean;

        BenchmarkInvocationHandler(List<Method> methods, Object bean) {
            this.originalBenchmarkMethods = methods;
            this.originalBean = bean;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            return isBenchmark(method)? callBenchmarkingMethod(method, args) : method.invoke(this.originalBean,args);
        }

        private Object callBenchmarkingMethod(Method method, Object[] args) throws IllegalAccessException, InvocationTargetException {
            double before = System.nanoTime();
            Object result = method.invoke(this.originalBean, args);
            double after = System.nanoTime();
            System.out.println(method + " : " + (after - before));
            return result;
        }

        private boolean isBenchmark(Method m){
            return this.originalBenchmarkMethods
                    .stream()
                    .anyMatch(method -> m.getName().equals(method.getName()));
        }
    }


    @Override
    public BeanDefinition getBeanDefinition(String beanName) {
        return config.containsKey(beanName) ? new BeanDefinition(beanName) : null;
    }

    @Override
    public BeanDefinition[] getBeanDefinitionNames() {
        return this.config.entrySet()
                .stream()
                .map(Map.Entry::getKey)
                .map(BeanDefinition::new)
                .toArray(BeanDefinition[]::new);
    }
}
