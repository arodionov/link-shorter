package shorter;

import shorter.model.Link;
import shorter.repository.InMemShortLinksRepo;
import shorter.repository.ShortLinksRepo;
import shorter.service.DefaultShortenLinkService;
import shorter.service.IdentShorterService;
import shorter.service.ShortenLinkService;
import shorter.service.ShorterService;

import java.util.Optional;

import static shorter.model.Link.linkTo;

public class ShorterApp {

    public static void main(String[] args) {
        String url = "https://www.facebook.com/groups/KyivKUG/";

        ShortLinksRepo inMemShortLinksRepo = new InMemShortLinksRepo();
        ShorterService shorterService = new IdentShorterService();
        ShortenLinkService shortenLinkService = new DefaultShortenLinkService(inMemShortLinksRepo, shorterService);
        Link shortLink = shortenLinkService.shortLink(linkTo(url));
        System.out.println("Short link: " + shortLink.link());

        Optional<Link> fullLink = shortenLinkService.fullLink(shortLink);
        System.out.println("Full link: " + fullLink.get().link());

    }

}
