package ioc;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JavaConfAppContext implements BeanFactory {

    private Map<String, Class<?>> config;
    private Map<String, Object> beans = new HashMap<>();

    public JavaConfAppContext(Map<String, Class<?>> config) {
        this.config = config;
    }

    public JavaConfAppContext() {
        this.config = Collections.emptyMap();
    }

    @Override
    public <T> T getBean(String beanName) {

        Object bean = beans.get(beanName);
        if (bean != null) return (T) bean;

        Class<?> beanClass = config.get(beanName);
        if (beanClass != null) {
            try {
                return (T) initNewBean(beanClass);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    @Override
    public BeanDefinition getBeanDefinition(String beanName) {
        return config.containsKey(beanName) ? new BeanDefinition(beanName) : null;
    }

    @Override
    public BeanDefinition[] getBeanDefinitionNames() {
        return config.entrySet().stream()
                .map(entry -> new BeanDefinition(entry.getKey()))
                .toArray(BeanDefinition[]::new);
    }

    private Object initNewBean(Class<?> beanClass) {
        Class<?>[] arguments = constructorArguments(beanClass);
        Object[] initargs = matchedBeansFromContext(arguments);
        try {
            Constructor<?> constructor = beanClass.getDeclaredConstructor(arguments);
            Object bean = constructor.newInstance(initargs);
            String beanName = beanNameFromClass(beanClass);
            beans.put(beanName, bean);
            checkInitMethod(beanClass);
            checkPostConstructBeanAnnotation(beanClass);
            wrapWithProxy(beanClass);
            return bean;
        } catch (Exception e) {
            throw new RuntimeException("Exception occurred during bean instantiation");
        }
    }

    private void wrapWithProxy(Class<?> beanClass) {
        String beanName = beanNameFromClass(beanClass);
        Object bean = beans.get(beanName);
        ClassLoader classLoader = bean.getClass().getClassLoader();
        Class<?>[] interfaces = bean.getClass().getInterfaces();
        List<String> annotatedMethodNames = benchmarkAnnotatedMethodNames(bean);
        if (!annotatedMethodNames.isEmpty()) {
            Object proxyInstance = Proxy.newProxyInstance(classLoader, interfaces, new BeanBenchmarkInvocationHandler(bean, annotatedMethodNames));
            beans.put(beanName, proxyInstance);
        }
    }

    private List<String> benchmarkAnnotatedMethodNames(Object bean) {
        Method[] methods = bean.getClass().getMethods();
        return Stream.of(methods)
                .filter(method -> method.isAnnotationPresent(Benchmark.class))
                .map(Method::getName)
                .collect(Collectors.toList());
    }

    private void checkInitMethod(Class<?> beanClass) {
        try {
            Method initMethod = beanClass.getMethod("init");
            if (initMethod != null) {
                String beanName = beanNameFromClass(beanClass);
                Object o = beans.get(beanName);
                initMethod.invoke(o);
            }
        } catch (Exception e) {
            //¯\_(ツ)_/¯
        }
    }

    private Object[] matchedBeansFromContext(Class<?>[] constructorArguments) {
        Object[] injectionBeans = new Object[constructorArguments.length];
        for (int i = 0; i < constructorArguments.length; i++) {
            Class<?> argument = constructorArguments[i];
            String beanName = beanNameFromClass(argument);
            Class<?> concreteClass = config.get(beanName);
            checkRequiredArguments(concreteClass);
            injectionBeans[i] = beans.get(beanName);
        }
        return injectionBeans;
    }

    private void checkPostConstructBeanAnnotation(Class<?> beanClass) {
        Method[] declaredMethods = beanClass.getDeclaredMethods();
        for (Method declaredMethod : declaredMethods) {
            if (declaredMethod.isAnnotationPresent(PostConstructBean.class)) {
                String beanName = beanNameFromClass(beanClass);
                Object o = beans.get(beanName);
                try {
                    declaredMethod.invoke(o);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void checkRequiredArguments(Class<?> beanClass) {
        Class[] arguments = constructorArguments(beanClass);
        if (arguments.length == 0) {
            initNewBean(beanClass);
        } else {
            for (Class<?> argument : arguments) {
                String beanName = beanNameFromClass(argument);
                Object bean = beans.get(beanName);
                if (bean == null) {
                    initNewBean(argument);
                }
            }
        }
    }

    private Class[] constructorArguments(Class<?> beanClass) {
        Constructor<?>[] constructors = beanClass.getDeclaredConstructors();
        if (constructors.length > 1) {
            String message = "There must be only one constructor to instantiate " + beanClass.getSimpleName();
            throw new RuntimeException(message);
        }
        Constructor<?> constructor = constructors[0];
        return constructor.getParameterTypes();
    }

    private String beanNameFromClass(Class<?> beanClass) {
        Class<?>[] interfaces = beanClass.getInterfaces();
        String simpleName = beanClass.getSimpleName();
        if (interfaces.length != 0) {
            simpleName = interfaces[0].getSimpleName(); // to simplify implementation
        }
        return simpleName.substring(0, 1).toLowerCase() + simpleName.substring(1);
    }

    private class BeanBenchmarkInvocationHandler implements InvocationHandler {
        private Object originalBean;
        private List<String> annotatedMethods;

        BeanBenchmarkInvocationHandler(Object originalBean, List<String> annotatedMethodNames) {
            this.originalBean = originalBean;
            this.annotatedMethods = annotatedMethodNames;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (annotatedMethods.contains(method.getName())) {
                long nanosBefore = System.nanoTime();
                Object resultProxy = method.invoke(originalBean, args);
                long nanosAfter = System.nanoTime();
                String message = String.format(
                        "Execution time of %s.%s is %d",
                        originalBean.getClass().getSimpleName(),
                        method.getName(),
                        nanosAfter - nanosBefore
                );
                System.err.println(message);
                return resultProxy;
            }
            return originalBean;
        }
    }
}
