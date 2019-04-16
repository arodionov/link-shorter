package ioc;

import ioc.service.BeanFactory;
import ioc.service.impl.JavaConfigAppContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import shorter.repository.impl.InMemShortLinksRepo;
import shorter.service.ShortenLinkService;
import shorter.service.ShorterService;
import shorter.service.impl.IdentShorterService;

import java.util.Map;

public class SpringApp {

    public static void main(String[] args) {

        Map<String, Class<?>> config = Map.of("shorterService", IdentShorterService.class, "linksRepo", InMemShortLinksRepo.class);
        BeanFactory context = new JavaConfigAppContext(config);
        ShorterService service = context.getBean("shorterService");
        InMemShortLinksRepo linksRepo = context.getBean("linksRepo");

//			AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
//			ctx.getBean("beanName");
//			ctx.getBeanDefinition("beanName");
//			ctx.getBeanDefinitionNames();


    }

}
