package ioc.service.impl;

import ioc.service.BeanFactory;
import util.BeanDefinition;
import util.exception.CycleDependencyException;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.util.Collections.emptyMap;
import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;
import static java.util.function.Function.identity;

@SuppressWarnings("all")
public class JavaConfigAppContext implements BeanFactory {

    private Map<String, Class<?>> config;
    private Map<String, Object> beans = new HashMap<>();
    private Map<String, String> invocations = new HashMap<>();

    public JavaConfigAppContext(Map<String, Class<?>> config) {
        this.config = config;
    }

    public JavaConfigAppContext() {
        this.config = emptyMap();
    }

    @Override
    public <T> T getBean(String beanName) {

        return (T) ofNullable(getBeanFromContext(beanName)).map(identity()).orElseGet(() -> {

            Class<?> beanClass = this.config.get(beanName);
            return initBean(beanClass, beanName);
        });
    }

    @Override
    public BeanDefinition getBeanDefinition(String beanName) {
        return this.config.containsKey(beanName) ? new BeanDefinition(beanName) : null;
    }

    @Override
    public BeanDefinition[] getBeanDefinitionNames() {
        return this.config.entrySet().stream()
                .map(entry -> new BeanDefinition(entry.getKey()))
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

                            checkInvocations(paramType, beanName);
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
        String[] path = clazz.getName().split("\\.");
        return path[path.length - 1].toLowerCase();
    }

    private String generateBeanName(String clazzName) {
        String[] path = clazzName.split("\\.");
        return path[path.length - 1].toLowerCase();
    }

    private void checkInvocations(Class<?> clazz, String beanName) {
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
        return this.config.entrySet().stream()
                .filter(entry -> entry.getValue().equals(clazz))
                .map(Map.Entry::getKey)
                .findAny();
    }

    private Optional<String> getBeanNameFromContext(String fullClassName) {
        return this.config.entrySet().stream()
                .filter(entry -> entry.getValue().getName().equals(fullClassName))
                .map(Map.Entry::getKey)
                .findAny();
    }
}