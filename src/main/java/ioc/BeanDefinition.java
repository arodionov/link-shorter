package ioc;

public class BeanDefinition {

	private String beanName;
	private Class<?> beanClass;

	public BeanDefinition(String name, Class<?> aClass) {
		beanName = name;
		beanClass = aClass;
	}

	public Class<?> getBeanClass() {
		return beanClass;
	}

	public String getBeanName() {
		return beanName;
	}
}
