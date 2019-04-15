package shorter;

import ioc.BeanFactory;
import ioc.JavaConfAppContext;
import shorter.model.Link;
import shorter.repo.InMemShortLinksRepo;
import shorter.service.DefaultShortenLinkService;
import shorter.service.IdentShorterService;
import shorter.service.ShortenLinkService;

import java.util.Map;
import java.util.Optional;

import static shorter.model.Link.linkTo;

public class ShrorterApp {

	public static void main(String[] args) {
		String url = "https://www.facebook.com/groups/KyivKUG/";

		Map<String, Class<?>> beanDefinitions = Map.of(
				"shortLinksRepo", InMemShortLinksRepo.class,
				"shorterService", IdentShorterService.class,
				"shortenLinkService", DefaultShortenLinkService.class);
		BeanFactory context = new JavaConfAppContext(beanDefinitions);

		ShortenLinkService shortenLinkService = context.getBean("shortenLinkService");
		Link shortLink = shortenLinkService.shortLink(linkTo(url));
		System.out.println("Short link: " + shortLink.link());

		Optional<Link> fullLink = shortenLinkService.fullLink(shortLink);
		System.out.println("Full link: " + fullLink.get().link());
	}

}
