package util;

import lombok.*;

@ToString
@Getter
@AllArgsConstructor
public class BeanDefinition {

    private String beanName;
    private Class<?> beanClass;
}