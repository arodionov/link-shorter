package shorter;

import shorter.model.Link;
import shorter.repo.InMemShortLinksRepo;
import shorter.repo.ShortLinksRepo;
import shorter.service.DefaultShortenLinkService;
import shorter.service.IdentShorterService;
import shorter.service.ShortenLinkService;
import shorter.service.ShorterService;

import java.util.Optional;

import static shorter.model.Link.linkTo;

public class ShrorterApp {

	public static void main(String[] args) {
		String url = "https://www.facebook.com/groups/KyivKUG/";


		ShorterService shorterService = new IdentShorterService();
		ShortLinksRepo shorterRepo = new InMemShortLinksRepo();

		ShortenLinkService shortenLinkService = new DefaultShortenLinkService(shorterService, shorterRepo);

		Link shortLink = shortenLinkService.shortLink(linkTo(url));
		System.out.println("Short link: " + shortLink.link());

		Optional<Link> fullLink = shortenLinkService.fullLink(shortLink);
		System.out.println("Full link: " + fullLink.get().link());

	}

}
