package shorter;

import ioc.BeanFactory;
import ioc.JavaConfAppContext;
import shorter.model.Link;
import shorter.repo.InMemShortLinksRepo;
import shorter.repo.ShortLinksRepo;
import shorter.service.DefaultShortenLinkService;
import shorter.service.IdentShorterService;
import shorter.service.ShortenLinkService;
import shorter.service.ShorterService;

import java.util.Map;
import java.util.Optional;

import static shorter.model.Link.linkTo;

public class ShrorterApp {

	public static void main(String[] args) {
		String url = "https://www.facebook.com/groups/KyivKUG/";

		Map<String, Class<?>> beanDefinitions = Map.of(
				"shortLinksRepo", InMemShortLinksRepo.class,
				"shorterService", IdentShorterService.class);
		BeanFactory context = new JavaConfAppContext(beanDefinitions);

		ShortLinksRepo repo = context.getBean("shortLinksRepo");
		ShorterService service = context.getBean("shorterService");

		ShortenLinkService shortenLinkService = new DefaultShortenLinkService(repo, service);
		Link shortLink = shortenLinkService.shortLink(linkTo(url));
		System.out.println("Short link: " + shortLink.link());

		Optional<Link> fullLink = shortenLinkService.fullLink(shortLink);
		System.out.println("Full link: " + fullLink.get().link());

	}

}
