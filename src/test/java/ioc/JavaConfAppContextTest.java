package ioc;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import shorter.service.IdentShorterService;
import shorter.service.ShorterService;

import static org.junit.Assert.*;

public class JavaConfAppContextTest {

	@Test
	public void createEmptyContext() {
		BeanFactory context = new JavaConfAppContext();
		assertNotNull(context);
	}

	@Test
	public void BDisEmptyForEmptyContext() {
		BeanFactory context = new JavaConfAppContext();
		BeanDefinition[] names = context.getBeanDefinitionNames();
		assertEquals(0, names.length);
	}

	@Test
	public void BDisNotEmptyForContext() {
		Map<String, Class<?>> config = new HashMap<String, Class<?>>() {{
			put("string", String.class);
		}};
		BeanFactory context = new JavaConfAppContext(config);
		BeanDefinition[] names = context.getBeanDefinitionNames();
		assertEquals(1, names.length);
	}

	@Test
	public void beanDefinition() {
		Map<String, Class<?>> config = new HashMap<String, Class<?>>() {{
			put("shorterService", IdentShorterService.class);
		}};
		BeanFactory context = new JavaConfAppContext(config);
		BeanDefinition name = context.getBeanDefinition("shorterService");
		assertNotNull(name);
	}

	@Test
	public void getBeanWithoutDeps() {
		Map<String, Class<?>> config = new HashMap<String, Class<?>>() {{
			put("shorterService", IdentShorterService.class);
		}};
		BeanFactory context = new JavaConfAppContext(config);
		ShorterService shorterService = context.getBean("shorterService");
		assertNotNull(shorterService);
	}

	@Test
	public void getBeanAreSame() {
		Map<String, Class<?>> config = new HashMap<String, Class<?>>() {{
			put("shorterService", IdentShorterService.class);
		}};
		BeanFactory context = new JavaConfAppContext(config);
		ShorterService shorterService1 = context.getBean("shorterService");
		ShorterService shorterService2 = context.getBean("shorterService");
		assertSame(shorterService1, shorterService2);
	}

}