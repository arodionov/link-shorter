package ioc;

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
		if(beanClass != null) {
			try {
				bean = beanClass.newInstance();
				beans.put(beanName, bean);
				return (T) bean;
			}
			catch (Exception e) {
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
}
