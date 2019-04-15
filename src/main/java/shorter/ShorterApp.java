package shorter;

import shorter.model.Link;
import shorter.service.DefaultShortenLinkService;
import shorter.service.ShortenLinkService;

import java.util.Optional;

import static shorter.model.Link.linkTo;

public class ShorterApp {

	public static void main(String[] args) {
		String url = "https://www.facebook.com/groups/KyivKUG/";

		ShortenLinkService shortenLinkService = new DefaultShortenLinkService();
		Link shortLink = shortenLinkService.shortLink(linkTo(url));
		System.out.println("Short link: " + shortLink.link());

		Optional<Link> fullLink = shortenLinkService.fullLink(shortLink);
		System.out.println("Full link: " + fullLink.get().link());

	}

}
