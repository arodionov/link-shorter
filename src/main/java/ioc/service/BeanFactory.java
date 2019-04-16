package ioc.service;

public interface BeanFactory {

    <T> T getBean(String beanName);

    BeanDefinition getBeanDefinition(String beanName);

    BeanDefinition[] getBeanDefinitionNames();
}