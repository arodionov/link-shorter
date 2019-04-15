package ioc;

public class JavaConfAppContext implements BeanFactory {

    @Override
    public <T> T getBean(final String beanName) {
        return null;
    }

    @Override
    public BeanDefinition getBeanDefinition(final String beanName) {
        return null;
    }

    @Override
    public BeanDefinition[] getBeanDefinitionNames() {
        return new BeanDefinition[0];
    }
}
