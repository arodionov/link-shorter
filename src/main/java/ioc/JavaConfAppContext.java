package ioc;

import java.util.Map;

public class JavaConfAppContext implements BeanFactory {

    private final Map<String, Class<?>> config;

    public JavaConfAppContext() {
        this.config = Map.of();
    }

    public JavaConfAppContext(final Map<String, Class<?>> config) {
        this.config = config;
    }

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
        return config.keySet().stream()
                .map(BeanDefinition::new)
                .toArray(BeanDefinition[]::new);
    }
}
