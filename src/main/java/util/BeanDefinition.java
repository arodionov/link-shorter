package util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
public class BeanDefinition {

    private String beanName;

    private Class<?> beanClass;
}