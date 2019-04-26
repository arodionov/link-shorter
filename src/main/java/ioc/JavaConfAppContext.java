package ioc;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class JavaConfAppContext implements BeanFactory {

	private Map<String, BeanDefinition> beanDefinitions;
	private Map<String, Object> beans = new HashMap<>();

	public JavaConfAppContext(Map<String, Class<?>> config) {
		beanDefinitions = config.entrySet().stream()
		                        .map(entry -> new BeanDefinition(entry.getKey(), entry.getValue()))
		                        .collect(Collectors.toMap(
		                        		BeanDefinition::getBeanName,
				                        Function.identity())
		                        );

	}

	public JavaConfAppContext() {
		this.beanDefinitions = Collections.emptyMap();
	}

	@Override
	public <T> T getBean(String beanName) {
		try {
			return (T) getBean0(beanName);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private Object getBean0(String beanName) throws Exception {

		Object bean = beans.get(beanName);
		if (bean != null) {
			return bean;
		}

		Class<?> type = getBeanType(beanName);
		if (type == null) {
			throw new RuntimeException("Bean not found");
		}

		BeanBuilder builder = new BeanBuilder(type);
		builder.createBean();
		builder.createBeanBenchmarkProxy();
		builder.callPostConstructBeanMethod();
		builder.callInitMethod();
		bean = builder.build();

		beans.put(beanName, bean);
		return bean;
	}

	private Class<?> getBeanType(String beanName) {
		return beanDefinitions.get(beanName).getBeanClass();
	}

	@Override
	public BeanDefinition getBeanDefinition(String beanName) {
		return beanDefinitions.get(beanName);
	}

	@Override
	public BeanDefinition[] getBeanDefinitionNames() {
		return beanDefinitions.values().toArray(new BeanDefinition[0]);
	}

	class BeanBuilder {
		private final Class<?> beanType;
		private Object bean;
		private Object proxyBean;

		public BeanBuilder(Class<?> beanType) {
			this.beanType = beanType;
		}

		public void createBean() throws Exception {
			bean = hasConstructorWithParams() ?
					newInstanceWithParameters() :
					newInstanceWithoutParams();
		}

		private boolean hasConstructorWithParams() {
			Constructor<?> constructor = beanType.getConstructors()[0];
			return constructor.getParameterCount() > 0;
		}

		private Object newInstanceWithoutParams() throws InstantiationException, IllegalAccessException {
			return beanType.newInstance();
		}

		private Object newInstanceWithParameters() throws Exception {
			Constructor<?> constructor = beanType.getConstructors()[0];
			Parameter[] parameters = constructor.getParameters();
			Object[] paramsVal = constructorParams(parameters);
			return constructor.newInstance(paramsVal);
		}

		private Object[] constructorParams(Parameter[] parameters) {
			Object[] paramsVal = new Object[parameters.length];

			for (int i = 0; i < parameters.length; i++) {
				Class<?> paramType = parameters[i].getType();
				String beanName = getBeanNameByType(paramType);
				paramsVal[i] = getBean(beanName);
			}
			return paramsVal;
		}

		private String getBeanNameByType(Class<?> paramType) {
			String paramTypeName = paramType.getSimpleName();
			String localBeanName
					= firstCharToLowerCase(paramTypeName);
			return localBeanName;
		}

		private String firstCharToLowerCase(String paramTypeName) {
			return Character.toLowerCase(paramTypeName.charAt(0)) + paramTypeName.substring(1);
		}

		public void callInitMethod() throws Exception {
			Method method;
			try {
				method = beanType.getMethod("init");
			} catch (NoSuchMethodException ex) {
				return;
			}
			method.invoke(bean);
		}

		public void callPostConstructBeanMethod() throws Exception{
			Method[] methods = beanType.getMethods();
			for (Method m : methods) {
				if (m.isAnnotationPresent(PostConstructBean.class)) {
					m.invoke(bean);
				}
			}
		}

		public void createBeanBenchmarkProxy() {

			proxyBean = Proxy.newProxyInstance(
					beanType.getClassLoader(),
					beanType.getInterfaces(),
					new InvocationHandler() {
						@Override
						public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
							double start = System.nanoTime();
							Object res = method.invoke(bean, args);
							double end = System.nanoTime();
							System.out.println(method + ": " + (end - start));
							return res;
						};
					});

		}

		public Object build() {
			return proxyBean == null ? bean : proxyBean;
		}
	}
}
