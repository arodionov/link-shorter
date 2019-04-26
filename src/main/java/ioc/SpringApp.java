package ioc;

import java.util.HashMap;
import java.util.Map;

import shorter.repo.InMemShortLinksRepo;
import shorter.repo.ShortLinksRepo;
import shorter.service.IdentShorterService;
import shorter.service.ShorterService;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringApp {

	public static void main(String[] args) {


		Map<String, Class<?>> config = new HashMap<String, Class<?>>() {{
			put("shorterService", IdentShorterService.class);
			put("linksRepo", InMemShortLinksRepo.class);
		}};
		BeanFactory context = new JavaConfAppContext(config);


//			AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
//			ctx.getBean("beanName");
//			ctx.getBeanDefinition("beanName");
//			ctx.getBeanDefinitionNames();


	}

}
