package ioc.service;

import util.BeanDefinition;

public interface BeanFactory {

    <T> T getBean(String beanName);

    BeanDefinition getBeanDefinition(String beanName);

    BeanDefinition[] getBeanDefinitionNames();
}