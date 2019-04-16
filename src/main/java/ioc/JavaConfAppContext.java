package ioc;

import ioc.anotation.Benchmark;
import ioc.anotation.PostConstructBean;

import java.lang.reflect.*;
import java.util.*;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.Comparator.comparing;

public class JavaConfAppContext implements BeanFactory {

    private Map<String, Class<?>> config;
    private Map<String, Object> beansByName;
    private Map<String, String> circleDependenciesIndicator;

    public JavaConfAppContext(Map<String, Class<?>> config) {
        this.config = config;
        this.beansByName = new HashMap<>();
        this.circleDependenciesIndicator = new HashMap<>();
        initializeBeans();
    }

    public JavaConfAppContext() {
        this.config = new HashMap<>();
    }

    private void initializeBeans() {

        config.forEach((beanName, clazz) -> {
            if (!beansByName.containsKey(beanName)) {
                initBeanRecursively(beanName, clazz);
            }
        });
    }

    private Object initBeanRecursively(String beanName, Class<?> beanClazz) {
        Constructor<?> constructor = getAppropriateConstructor(beanClazz);
        LinkedList<Object> params = new LinkedList<>();

        for (Parameter parameter : constructor.getParameters()) {

            String parameterName = getBeanName(parameter.getType());
            Class<?> implClass = parameterValidation(beanName, parameterName, parameter.getType());

            getBean(beanName, implClass).ifPresentOrElse(
                    params::add,
                    () -> {
                        circleDependenciesIndicator.put(beanName, parameterName);
                        params.add(initBeanRecursively(parameterName, implClass));
                    });
        }

        Object bean = initBean(beanName, constructor, params);
        callInit(bean);
        createBenchmarkProxy(beanName, bean);

        return bean;
    }

    private Constructor<?> getAppropriateConstructor(Class<?> clazz) {
        return stream(clazz.getConstructors()).min(comparing(Constructor::getParameterCount))
                .orElseThrow(() -> {
                    String message = format("Unable to recognize constructor for '%s'", clazz.getSimpleName());
                    throw new RuntimeException(message);
                });
    }

    private String getBeanName(Class<?> clazz) {
        char[] c = clazz.getSimpleName().toCharArray();
        c[0] = Character.toLowerCase(c[0]);
        return new String(c);
    }

    private Optional<Object> getBean(String beanName, Class<?> clazz) {
        if (beansByName.containsKey(beanName)) {
            return Optional.of(beansByName.get(beanName));
        } else {
            return beansByName.values().stream()
                    .filter(bean -> asList(bean.getClass().getInterfaces()).containsAll(asList(clazz.getInterfaces())))
                    .findAny();
        }
    }

    private Class<?> parameterValidation(String beanName, String requiredParameterName, Class<?> requiredParameterClass) {
        Class<?> implClass = config.values().stream()
                .filter(contextBeans -> asList(contextBeans.getInterfaces()).contains(requiredParameterClass))
                .findAny().orElseThrow(() -> {
                    String message = format("Dependency '%s' required for '%s' not found",
                            requiredParameterClass.getSimpleName(), beanName);
                    throw new RuntimeException(message);
                });

        if (circleDependenciesIndicator.containsKey(beanName)) {
            String indicator = circleDependenciesIndicator.get(beanName);
            if (indicator.equals(requiredParameterName)) {
                String message = format(
                        "Circular dependency is found between '%s' and '%s'", beanName, indicator);
                throw new RuntimeException(message);
            }
        }

        return implClass;
    }

    private Object initBean(String beanName, Constructor<?> constructor, List<Object> paramsList) {
        Object[] params = paramsList.toArray(Object[]::new);
        try {
            Object newBean = constructor.newInstance(params);
            beansByName.put(beanName, newBean);
            return newBean;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private <T> void callInit(T bean) {
        try {
            for (Method method : bean.getClass().getDeclaredMethods()) {
                if (method.getName().equals("init") || method.isAnnotationPresent(PostConstructBean.class)) {
                    method.invoke(bean);
                    break;
                }
            }
        } catch (IllegalAccessException | InvocationTargetException ignore) {
        }
    }

    private void createBenchmarkProxy(String beanName, Object bean) {
        Object proxyBean = Proxy.newProxyInstance(
                bean.getClass().getClassLoader(),
                bean.getClass().getInterfaces(),
                ((proxy, method, args) -> {
                    if (method.isAnnotationPresent(Benchmark.class)) {
                        long timeBefore = System.currentTimeMillis();
                        Object result = method.invoke(bean, args);
                        System.out.println(method.getName() + ": executionTime = " + (System.currentTimeMillis() - timeBefore));
                        return result;
                    } else {
                        return method.invoke(bean, args);
                    }
                }));
        beansByName.put(beanName, proxyBean);
    }

    @Override
    public <T> T getBean(String beanName) {
        Object bean = beansByName.get(beanName);
        return bean != null ? (T) bean : null;
    }

    @Override
    public BeanDefinition getBeanDefinition(String beanName) {
        return config.containsKey(beanName) ? new BeanDefinition(beanName) : null;
    }

    @Override
    public BeanDefinition[] getBeanDefinitionNames() {
        return config.keySet().stream()
                .map(BeanDefinition::new)
                .toArray(BeanDefinition[]::new);
    }
}
