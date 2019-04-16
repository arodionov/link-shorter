package shorter;

import ioc.service.BeanFactory;
import ioc.service.impl.JavaConfigAppContext;
import shorter.model.Link;
import shorter.repository.impl.InMemShortLinksRepo;
import shorter.repository.ShortLinksRepo;
import shorter.service.impl.DefaultShortenLinkService;
import shorter.service.impl.IdentShorterService;
import shorter.service.ShortenLinkService;
import shorter.service.ShorterService;

import java.util.Map;
import java.util.Optional;

import static shorter.model.Link.linkTo;

public class ShorterApp {

    public static void main(String[] args) {

        /**
         * configure with dependencies (find in context - if found - then return, if not - create new)
         */
        String url = "https://www.facebook.com/groups/KyivKUG/";

        Map<String, Class<?>> config = Map.of("shorterService", IdentShorterService.class, "linksRepo", InMemShortLinksRepo.class);
        BeanFactory context = new JavaConfigAppContext(config);
        ShorterService service = context.getBean("shorterService");
        InMemShortLinksRepo linksRepo = context.getBean("linksRepo");

//        ShortLinksRepo shortLinksRepo = new InMemShortLinksRepo();
//        ShorterService shortenService = new IdentShorterService();
//
        ShortenLinkService shortenLinkService = new DefaultShortenLinkService(linksRepo, service);
        Link shortLink = shortenLinkService.shortLink(linkTo(url));
        System.out.println("Short link: " + shortLink.link());

        Optional<Link> fullLink = shortenLinkService.fullLink(shortLink);
        System.out.println("Full link: " + fullLink.get().link());

    }

}