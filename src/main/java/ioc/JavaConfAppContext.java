package ioc;

import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class JavaConfAppContext implements BeanFactory {

    private Map<String, Class<?>> config;
    private Map<String, Object> beans = new HashMap<>();

    public JavaConfAppContext(Map<String, Class<?>> config) {
        this.config = config;
    }

    public JavaConfAppContext() {
        this.config = Collections.emptyMap();
    }

    @Override
    public <T> T getBean(String beanName) {

        Object bean = beans.get(beanName);
        if (bean != null) return (T) bean;

        Class<?> beanClass = config.get(beanName);
        if (beanClass != null) {
            try {
                return (T) initNewBean(beanClass);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    @Override
    public BeanDefinition getBeanDefinition(String beanName) {
        return config.containsKey(beanName) ? new BeanDefinition(beanName) : null;
    }

    @Override
    public BeanDefinition[] getBeanDefinitionNames() {
        return config.entrySet().stream()
                .map(entry -> new BeanDefinition(entry.getKey()))
                .toArray(BeanDefinition[]::new);
    }

    private Object initNewBean(Class<?> beanClass) {
        Class<?>[] arguments = constructorArguments(beanClass);
        Object[] initargs = matchedBeansFromContext(arguments);
        try {
            Constructor<?> constructor = beanClass.getDeclaredConstructor(arguments);
            Object bean = constructor.newInstance(initargs);
            String beanName = beanNameFromClass(beanClass);
            beans.put(beanName, bean);
            return bean;
        } catch (Exception e) {
            throw new RuntimeException("Exception occurred during bean instantiation");
        }
    }

    private Object[] matchedBeansFromContext(Class<?>[] constructorArguments) {
        Object[] injectionBeans = new Object[constructorArguments.length];
        for (int i = 0; i < constructorArguments.length; i++) {
            Class<?> argument = constructorArguments[i];
            String beanName = beanNameFromClass(argument);
            Class<?> concreteClass = config.get(beanName);
            checkRequiredArguments(concreteClass);
            injectionBeans[i] = beans.get(beanName);
        }
        return injectionBeans;
    }

    private void checkRequiredArguments(Class<?> beanClass) {
        Class[] arguments = constructorArguments(beanClass);
        if (arguments.length == 0) {
            initNewBean(beanClass);
        } else {
            for (Class<?> argument : arguments) {
                String beanName = beanNameFromClass(argument);
                Object bean = beans.get(beanName);
                if (bean == null) {
                    initNewBean(argument);
                }
            }
        }
    }

    private Class[] constructorArguments(Class<?> beanClass) {
        Constructor<?>[] constructors = beanClass.getDeclaredConstructors();
        if (constructors.length > 1) {
            String message = "There must be only one constructor to instantiate " + beanClass.getSimpleName();
            throw new RuntimeException(message);
        }
        Constructor<?> constructor = constructors[0];
        return constructor.getParameterTypes();
    }

    private String beanNameFromClass(Class<?> beanClass) {
        Class<?>[] interfaces = beanClass.getInterfaces();
        String simpleName = beanClass.getSimpleName();
        if (interfaces.length != 0) {
            simpleName = interfaces[0].getSimpleName(); // to simplify implementation
        }
        return simpleName.substring(0, 1).toLowerCase() + simpleName.substring(1);
    }
}
