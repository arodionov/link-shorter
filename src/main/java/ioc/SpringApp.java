package ioc;

import shorter.model.Link;
import shorter.repo.InMemShortLinksRepo;
import shorter.service.DefaultShortenLinkService;
import shorter.service.IdentShorterService;
import shorter.service.ShortenLinkService;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static shorter.model.Link.linkTo;

public class SpringApp {

	public static void main(String[] args) {

		String url = "https://www.facebook.com/groups/KyivKUG/";

		Map<String, Class<?>> config = new HashMap<String, Class<?>>(){{
			put("shorterService", IdentShorterService.class);
			put("shortLinksRepo", InMemShortLinksRepo.class);
			put("shortenLinkService", DefaultShortenLinkService.class);
		}};

		BeanFactory context = new JavaConfAppContext(config);

//		ShorterService shorterService = context.getBean("shortnerService");
//		ShortLinksRepo shorterRepo = context.getBean("linksRepo");

		ShortenLinkService shortenLinkService = context.getBean("shortenLinkService");

		Link shortLink = shortenLinkService.shortLink(linkTo(url));
		System.out.println("Short link: " + shortLink.link());

		Optional<Link> fullLink = shortenLinkService.fullLink(shortLink);
		System.out.println("Full link: " + fullLink.get().link());


//			AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
//			ctx.getBean("beanName");
//			ctx.getBeanDefinition("beanName");
//			ctx.getBeanDefinitionNames();
//

	}

}
