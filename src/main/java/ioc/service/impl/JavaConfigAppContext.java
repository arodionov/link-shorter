package ioc.service.impl;

import ioc.service.BeanDefinition;
import ioc.service.BeanFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class JavaConfigAppContext implements BeanFactory {

    private Map<String, Class<?>> config;
    private Map<String, Object> beans = new HashMap<>();

    public JavaConfigAppContext(Map<String, Class<?>> config) {
        this.config = config;

    }

    public JavaConfigAppContext() {
        this.config = Collections.emptyMap();
    }

    @Override
    public <T> T getBean(String beanName) {

        Object beanObj = this.beans.get(beanName);
        if (beanName != null) {
            return (T) beanObj;
        }

        Class<?> beanClass = this.config.get(beanName);
        if (beanClass != null) {
            try {
                beanObj = (T) beanClass.getConstructor().newInstance();
                this.beans.put(beanName, beanObj);
                return (T) beanObj;
            } catch (Exception e) {
                e.printStackTrace();
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
        return this.config.entrySet().stream()
                .map(entry -> new BeanDefinition(entry.getKey()))
                .toArray(BeanDefinition[]::new);
    }
}