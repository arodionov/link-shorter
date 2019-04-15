package ioc;

public class BeanDefinition {

    private String beanName;

    public BeanDefinition(final String beanName) {
        this.beanName = beanName;
    }

    public String getBeanName() {
        return beanName;
    }
}
