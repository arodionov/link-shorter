package ioc;

public class BeanDefinition {

    private String beanName;

    public BeanDefinition(String beanName) {
        this.beanName = beanName;
    }

    public String getBeanName() {
        return beanName;
    }
}
