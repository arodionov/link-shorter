package ioc.service.impl;

import ioc.service.BeanFactory;
import lombok.Builder;
import util.exception.BeanNotFoundException;
import util.proxy.BeanProxy;
import util.annotation.PostConstructBean;
import util.BeanDefinition;
import util.exception.CycleDependencyException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.util.Collections.emptyMap;
import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

@SuppressWarnings("all")
public class JavaConfigAppContext implements BeanFactory {

    private Map<String, BeanDefinition> beanDefinitions;
    private Map<String, Object> beans = new HashMap<>();
    private Map<String, String> invocations = new HashMap<>();

    public JavaConfigAppContext(Map<String, Class<?>> beanDefinitions) {
        this.beanDefinitions = beanDefinitions.entrySet().stream()
                .collect(toMap(entry -> entry.getKey(),
                        entry -> new BeanDefinition(entry.getKey(), entry.getValue())));
    }

    public JavaConfigAppContext() {
        this.beanDefinitions = emptyMap();
    }

    @Override
    public <T> T getBean(String beanName) {

        checkIfBeanHasInitMetodOrElseThrowException(beanName);

        return (T) ofNullable(getBeanFromContext(beanName)).map(identity()).orElseGet(() -> {

            Class<?> beanClass = ofNullable(this.beanDefinitions.get(beanName))
                    .map(bean -> bean.getBeanClass()).orElse(null);

            Object bean = initBean(beanClass, beanName);

            invokeInitMethod(bean);
            checkPostConstructAnnotationAndInvoke(bean);

            return getProxyMethodWithBenchmarkAnnotation(bean, beanName);
        });
    }

    private Object getProxyMethodWithBenchmarkAnnotation(Object bean, String beanName) {
        Class<?> aClass = bean.getClass();
        BeanProxy beanProxy = new BeanProxy(aClass);
        Object proxy = Proxy.newProxyInstance(aClass.getClassLoader(), aClass.getInterfaces(), beanProxy);
        this.beans.put(beanName, proxy);
        return proxy;
    }

    private void checkPostConstructAnnotationAndInvoke(Object bean) {
        for (Method method : bean.getClass().getMethods()) {
            if (method.isAnnotationPresent(PostConstructBean.class)) {
                try {
                    method.invoke(bean);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void invokeInitMethod(Object bean) {
        try {
            bean.getClass().getMethod("init").invoke(bean);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkIfBeanHasInitMetodOrElseThrowException(String beanName) {
        String methodName = "init";
        boolean isPresent = false;
        Optional<BeanDefinition> aClass = ofNullable(this.beanDefinitions.get(beanName));
        if (aClass.isPresent()) {
            for (Method method : aClass.get().getBeanClass().getMethods()) {
                if (method.getName().equals(methodName)) {
                    isPresent = true;
                }
            }
        } else {
            throw new BeanNotFoundException("No bean with name '" + beanName + "' found");
        }
        if (!isPresent) {
            throw new RuntimeException("No method '" + methodName + "' present");
        }
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

    private <T> T initBean(Class<?> beanClass, String beanName) {

        if (beanClass != null) {
            try {
                Object bean = null;
                Constructor<?>[] constructors = beanClass.getConstructors();

                for (Constructor<?> constructor : constructors) {
                    Class<?>[] parameterTypes = constructor.getParameterTypes();

                    for (Class<?> paramType : parameterTypes) {

                        if (!isBeanPresentInContext(paramType)) {

                            validateInvocationsOrElseThrowException(paramType, beanName);
                            beanName = generateBeanName(paramType);

                            try {
                                Constructor<?> constructor1 = paramType.getConstructor();

                                Object createdBean = paramType.getConstructor().newInstance();
                                String name = getBeanNameFromContext(paramType).orElse(beanName);
                                this.beans.put(name, createdBean);
                            } catch (NoSuchMethodException e) {
                                initBean(paramType, beanName);
                            }
                        }
                    }
                    String clazzName = constructor.getName();

                    if (!isBeanPresentInContext(clazzName)) {
                        Object[] constructorParams = new Object[parameterTypes.length];
                        for (int i = 0; i < parameterTypes.length; i++) {
                            String beanNameAtContext = generateBeanName(parameterTypes[i]);
                            constructorParams[i] = this.beans.get(beanNameAtContext);
                        }
                        bean = constructor.newInstance(constructorParams);
                        String name = getBeanNameFromContext(clazzName).orElse(generateBeanName(clazzName));
                        this.beans.put(name, bean);
                    }
                }
                return (T) bean;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private String generateBeanName(Class<?> clazz) {
        String clazzName = clazz.getName();
        return generateBeanName(clazzName);
    }

    private String generateBeanName(String clazzName) {
        String[] path = clazzName.split("\\.");
        String type = path[path.length - 1];
        return Character.toLowerCase(type.charAt(0)) + type.substring(1);
    }

    private void validateInvocationsOrElseThrowException(Class<?> clazz, String beanName) {
        String key = generateBeanName(clazz);
        this.invocations.put(key, beanName);

        for (Map.Entry<String, String> entry : this.invocations.entrySet()) {
            String clazz1Name = entry.getKey();
            String clazz2Name = entry.getValue();
            if ((clazz1Name.equalsIgnoreCase(beanName)) && (clazz2Name.equalsIgnoreCase(key))) {
                throw new CycleDependencyException(
                        "There are cycle dependencies in your configuration. " + clazz1Name + " <--> " + clazz2Name);
            }
        }
    }

    private boolean isBeanPresentInContext(Class<?> clazz) {
        String beanName = generateBeanName(clazz);
        Object bean = this.beans.get(beanName);
        return isNull(bean) ? false : true;
    }

    private boolean isBeanPresentInContext(String clazzName) {
        String beanName = generateBeanName(clazzName);
        Object bean = this.beans.get(beanName);
        return isNull(bean) ? false : true;
    }

    private Object getBeanFromContext(String beanName) {
        return this.beans.get(beanName);
    }

    private Optional<String> getBeanNameFromContext(Class<?> clazz) {
        return this.beanDefinitions.entrySet().stream()
                .filter(entry -> entry.getValue().equals(clazz))
                .map(Map.Entry::getKey)
                .findAny();
    }

    private Optional<String> getBeanNameFromContext(String fullClassName) {
        return this.beanDefinitions.entrySet().stream()
                .filter(entry -> entry.getValue().getBeanClass().getName().equals(fullClassName))
                .map(Map.Entry::getKey)
                .findAny();
    }

    @Builder
    private static class BeanMetaInfo {
        private Class<?> beanClass;
        private String beanName;
        private Object bean;
        private Object proxyBean;
    }
}