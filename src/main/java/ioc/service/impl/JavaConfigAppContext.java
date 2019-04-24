package ioc.service.impl;

import ioc.service.BeanFactory;
import lombok.Getter;
import util.BeanDefinition;
import util.annotation.Benchmark;
import util.annotation.PostConstructBean;
import util.exception.BeanNotFoundException;
import util.exception.CycleDependencyException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Class.forName;
import static java.lang.System.nanoTime;
import static java.lang.System.out;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.Collections.emptyMap;
import static java.util.Optional.ofNullable;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

@SuppressWarnings("all")
public class JavaConfigAppContext implements BeanFactory {

    private Map<String, BeanDefinition> beanDefinitions;
    private Map<String, Bean> context = new HashMap<>();
    private Map<String, String> invocations = new HashMap<>();

    public JavaConfigAppContext(Map<String, Class<?>> beanDefinitions) {
        this.beanDefinitions = beanDefinitions.entrySet().stream()
                .collect(toMap(entry -> entry.getKey(), entry -> new BeanDefinition(entry.getKey(), entry.getValue())));
        initializeContext();
    }

    public JavaConfigAppContext() {
        this.beanDefinitions = emptyMap();
    }

    @Override
    public <T> T getBean(String beanName) {
        return (T) ofNullable(this.context.get(beanName)).map(bean -> {
            bean.invokeInitMethod();
            bean.invokePostConstructMethod();
            bean.initProxy();
            return bean.getProxyBean();
        }).orElseThrow(() -> new BeanNotFoundException("Bean '" + beanName + "' is not found in context"));
    }

    @Override
    public BeanDefinition getBeanDefinition(String beanName) {
        return this.beanDefinitions.get(beanName);
    }

    @Override
    public BeanDefinition[] getBeanDefinitionNames() {
        return this.beanDefinitions.entrySet().stream()
                .map(entry -> entry.getValue())
                .toArray(BeanDefinition[]::new);
    }

    private void initializeContext() {
        this.beanDefinitions.entrySet().stream().map(Map.Entry::getValue)
                .forEach(beanDefinition -> initBean(beanDefinition.getBeanClass(), beanDefinition.getBeanName()));
    }

    private void initBean(Class<?> beanClass, String beanName) {
        Constructor<?> constructor = findFirstConstructor(beanClass);

        ofNullable(constructor.getParameterCount() == 0 ? null : 0).ifPresentOrElse(param -> {
            stream(constructor.getParameters())
                    .filter(parameter -> !isBeanAlreadyInContext(parameter.getType()))
                    .map(Parameter::getType)
                    .forEach(parameter -> {

                        BeanDefinition beanDefinition = getNameFromBeanDefinitions(parameter);
                        Class<?> clazz = beanDefinition.getBeanClass();
                        Constructor<?> firstConstructor = findFirstConstructor(clazz);
                        if (firstConstructor.getParameterCount() == 0) {

                            createBeanAndPutToContext(clazz, beanDefinition.getBeanName());
                        } else {
                            validateInvocationsOrElseThrowCycleDepsException(clazz, beanDefinition.getBeanName());
                            initBean(clazz, beanDefinition.getBeanName());
                        }
                    });
            createBeanAndPutToContext(constructor, beanName);
        }, () -> createBeanAndPutToContext(beanClass, beanName));
    }

    private BeanDefinition getNameFromBeanDefinitions(Class<?> clazz) {
        return ofNullable(this.beanDefinitions.get(generateBeanName(clazz.getName())))
                .map(identity()).orElseGet(() -> getBeanDefinitionByClass(clazz));
    }

    private BeanDefinition getBeanDefinitionByClass(Class<?> clazz) {
        return this.beanDefinitions.entrySet().stream()
                .filter(entry -> entry.getValue().getBeanClass()
                        .equals(clazz) || asList(entry.getValue().getBeanClass().getInterfaces()).contains(clazz))
                .map(entry -> entry.getValue())
                .findAny()
                .orElseThrow(() -> new BeanNotFoundException("No beans in config"));
    }

    private void createBeanAndPutToContext(Constructor<?> constructor, String beanName) {
        try {
            Object[] intiParams = stream(constructor.getParameterTypes())
                    .map(initParam -> {
                        return ofNullable(this.context.get(generateBeanName(initParam.getName())))
                                .map(bean -> bean.getBeanObj())
                                .orElseGet(() -> {
                                    String name = getBeanDefinitionByClass(initParam).getBeanName();
                                    return this.context.get(name).getBeanObj();
                                });
                    }).toArray();

            Object beanObj = constructor.newInstance(intiParams);
            Bean bean = new Bean(forName(constructor.getName()), beanName, beanObj);
            this.context.put(beanName, bean);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createBeanAndPutToContext(Class<?> clazz, String generatedBeanName) {
        try {
            Object beanOdj = clazz.getConstructor().newInstance();
            Bean bean = new Bean(clazz, generatedBeanName, beanOdj);
            this.context.put(generatedBeanName, bean);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Constructor<?> findFirstConstructor(Class<?> beanClass) {
        return stream(beanClass.getConstructors()).findAny()
                .orElseThrow(() -> new BeanNotFoundException("No constructors present in '" + beanClass + "'"));
    }

    private boolean isBeanAlreadyInContext(Class<?> clazz) {
        return this.context.entrySet().stream()
                .filter(entry -> entry.getValue().getBeanClass().equals(clazz))
                .findAny().isPresent() ? true : false;
    }

    private String generateBeanName(String clazzName) {
        String[] path = clazzName.split("\\.");
        String type = path[path.length - 1];
        return Character.toLowerCase(type.charAt(0)) + type.substring(1);
    }

    private void validateInvocationsOrElseThrowCycleDepsException(Class<?> clazz, String beanName) {
        String key = generateBeanName(clazz.getName());
        this.invocations.put(key, beanName);

        this.invocations.entrySet().stream()
                .filter(entry -> entry.getKey().equalsIgnoreCase(beanName) && entry.getValue().equalsIgnoreCase(key))
                .findAny().ifPresent(entry -> {
            throw new CycleDependencyException("There are cycle dependencies in your configuration. ["
                    + entry.getKey() + " <--> " + entry.getValue() + "]");
        });
    }

    @Getter
    private static class Bean {

        private Class<?> beanClass;
        private String beanName;
        private Object beanObj;
        private Object proxyBean;

        public Bean(Class<?> beanClass, String beanName, Object beanObj) {
            this.beanClass = beanClass;
            this.beanName = beanName;
            this.beanObj = beanObj;
        }

        private void invokeInitMethod() {
            stream(this.beanClass.getMethods())
                    .filter(method -> method.getName().equals("init"))
                    .forEach(method -> invokeMethod(method, this.beanObj));
        }

        private void invokePostConstructMethod() {
            stream(this.beanClass.getMethods())
                    .filter(method -> method.isAnnotationPresent(PostConstructBean.class))
                    .forEach(method -> invokeMethod(method, this.beanObj));
        }

        public void initProxy() {
            this.proxyBean = Proxy.newProxyInstance(this.beanObj.getClass().getClassLoader(),
                    this.beanObj.getClass().getInterfaces(),
                    ((proxy, method, args) -> {
                        Object res;
                        if (method.isAnnotationPresent(Benchmark.class)) {
                            double start = nanoTime();
                            res = method.invoke(this.beanObj, args);
                            double end = nanoTime();
                            out.println(method + " - execution time is " + (end - start) + " ns");
                        } else {
                            res = method.invoke(this.beanObj, args);
                        }
                        return res;
                    }));
        }

        private void invokeMethod(Method method, Object object) {
            try {
                method.invoke(this.beanObj);
            } catch (Exception e) {
                throw new RuntimeException("Something went wrong");
            }
        }
    }
}