package ioc;

import java.util.HashMap;
import java.util.Map;

import ioc.service.BeanFactory;
import ioc.service.impl.JavaConfigAppContext;
import shorter.repository.ShortLinksRepo;
import shorter.repository.impl.InMemShortLinksRepo;
import shorter.service.ShorterService;
import shorter.service.impl.IdentShorterService;

@SuppressWarnings("all")
public class SpringApp {

    public static void main(String[] args) {

        Map<String, Class<?>> config = new HashMap<String, Class<?>>() {{
            put("shorterService", IdentShorterService.class);
            put("linksRepo", InMemShortLinksRepo.class);
        }};

        BeanFactory context = new JavaConfigAppContext(config);
        ShorterService service = context.getBean("shorterService");
        ShortLinksRepo repo = context.getBean("linksRepo");

//			AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
//			ctx.getBean("beanName");
//			ctx.getBeanDefinition("beanName");
//			ctx.getBeanDefinitionNames();

    }
}