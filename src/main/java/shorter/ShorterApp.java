package shorter;

import ioc.service.BeanFactory;
import ioc.service.impl.JavaConfigAppContext;
import shorter.model.Link;
import shorter.repository.impl.InMemShortLinksRepo;
import shorter.service.ShortenLinkService;
import shorter.service.impl.DefaultShortenLinkService;
import shorter.service.impl.IdentShorterService;

import java.util.Map;
import java.util.Optional;

import static java.util.Map.of;
import static shorter.model.Link.linkTo;

@SuppressWarnings("all")
public class ShorterApp {

    public static void main(String[] args) {

        /**
         * configure with dependencies (find in context - if found - then return, if not - create new)
         */
        String url = "https://www.facebook.com/groups/KyivKUG/";

        Map<String, Class<?>> config = of(
                "shorterService", IdentShorterService.class,
                "linksRepo", InMemShortLinksRepo.class);

        BeanFactory context = new JavaConfigAppContext(config);
        IdentShorterService service = context.getBean("shorterService");
        InMemShortLinksRepo linksRepo = context.getBean("linksRepo");


        ShortenLinkService shortenLinkService = new DefaultShortenLinkService(linksRepo, service);
        Link shortLink = shortenLinkService.shortLink(linkTo(url));
        System.out.println("Short link: " + shortLink.link());

        Optional<Link> fullLink = shortenLinkService.fullLink(shortLink);
        System.out.println("Full link: " + fullLink.get().link());
    }
}