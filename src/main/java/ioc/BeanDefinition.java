package ioc;

import lombok.Data;

@Data
public class BeanDefinition {

    private String beanName;

    public BeanDefinition(String name) {
        beanName = name;
    }

}
