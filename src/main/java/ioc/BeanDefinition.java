package ioc;

public class BeanDefinition {

	private String beanName;

	public BeanDefinition(String name) {
		beanName = name;
	}

	public String getBeanName() {
		return beanName;
	}
}
