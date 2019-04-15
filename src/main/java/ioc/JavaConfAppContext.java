package ioc;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

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
        T cached = (T) cachedInstances.get(beanName);
        if (cached == null) {
            T beanClass = Optional.ofNullable(beanDefinitions.get(beanName))
                .map(cls -> (T) newInstance(cls))
                .orElseThrow(() -> new NoSuchBeanDefinitionException("Bean " + beanName + " was not found in context"));
            cachedInstances.put(beanName, beanClass);
            return beanClass;
        } else {
            return cached;
        }
    }

    /** Method is protected for other languages to override logic with their specific reflection,
     * like Scala Reflection for Scala */
    protected <T> T newInstance(Class<?> beanClass) {
        try {
            Class[] types = Arrays.stream(beanClass.getDeclaredConstructors()).findAny().stream()
                    .flatMap(constructor -> Arrays.stream(constructor.getParameterTypes()))
                    .toArray(Class[]::new);
            Object[] params = Arrays.stream(types)
                    .map(p -> this.getBean(getClassLowerCaseName(p)))
                    .toArray();
            return (T) beanClass.getDeclaredConstructor(types).newInstance(params);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new NoSuchBeanDefinitionException(e);
        }
    }

    private String getClassLowerCaseName(Class<?> cls) {
        String simpleName = cls.getSimpleName();
        return simpleName.substring(0, 1).toLowerCase() + simpleName.substring(1);
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
