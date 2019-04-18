package shorter;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import ioc.BeanFactory;
import ioc.JavaConfAppContext;
import shorter.model.Link;
import shorter.repo.InMemShortLinksRepo;
import shorter.repo.ShortLinksRepo;
import shorter.service.DefaultShortenLinkService;
import shorter.service.IdentShorterService;
import shorter.service.ShortenLinkService;
import shorter.service.ShorterService;

import static shorter.model.Link.linkTo;

public class ShrorterApp {

	public static void main(String[] args) {
		String url = "https://www.facebook.com/groups/KyivKUG/";


		Map<String, Class<?>> config = new HashMap<String, Class<?>>() {{
			put("shorterService", IdentShorterService.class);
			put("shortLinksRepo", InMemShortLinksRepo.class);
			put("shortenLinkService", DefaultShortenLinkService.class);
		}};
		BeanFactory context = new JavaConfAppContext(config);

//		ShortLinksRepo repo = new InMemShortLinksRepo();
//		ShorterService service = new IdentShorterService();
		ShortenLinkService shortenLinkService = context.getBean("shortenLinkService");

		Link shortLink = shortenLinkService.shortLink(linkTo(url));
		System.out.println("Short link: " + shortLink.link());

		Optional<Link> fullLink = shortenLinkService.fullLink(shortLink);
		System.out.println("Full link: " + fullLink.get().link());

	}

}
