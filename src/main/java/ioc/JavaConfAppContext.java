package ioc;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class JavaConfAppContext implements BeanFactory {

    private final Map<String, Class<?>> beanDefinitions;
    private final Map<String, Object> cachedInstances = new HashMap<>();

    public JavaConfAppContext() {
        this(Collections.emptyMap());
    }

    public JavaConfAppContext(Map<String, Class<?>> beanDefinitions) {
        this.beanDefinitions = beanDefinitions;
    }

    @Override
    public <T> T getBean(String beanName) {
        try {
            T cached = (T) cachedInstances.get(beanName);
            if (cached == null) {
                Class<?> beanClass = beanDefinitions.get(beanName);
                if (beanClass == null) {
                    return null;
                } else {
                    T newInstance = (T) beanClass.getDeclaredConstructor().newInstance();
                    cachedInstances.put(beanName, newInstance);
                    return newInstance;
                }
            } else {
                return cached;
            }
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new NoSuchBeanDefinitionException(e);
        }
    }

    @Override
    public BeanDefinition getBeanDefinition(String beanName) {
        return beanDefinitions.containsKey(beanName) ? new BeanDefinition(beanName) : null;
    }

    @Override
    public BeanDefinition[] getBeanDefinitionNames() {
        return beanDefinitions.keySet().stream().map(BeanDefinition::new).toArray(BeanDefinition[]::new);
    }
}
