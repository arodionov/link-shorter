package shorter;

import ioc.BeanFactory;
import ioc.JavaConfAppContext;
import shorter.model.Link;
import shorter.repository.InMemShortLinksRepo;
import shorter.repository.ShortLinksRepo;
import shorter.service.DefaultShortenLinkService;
import shorter.service.IdentShorterService;
import shorter.service.ShortenLinkService;
import shorter.service.ShorterService;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static shorter.model.Link.linkTo;

public class ShorterApp {

    public static void main(String[] args) {
        String url = "https://www.facebook.com/groups/KyivKUG/";

        Map<String, Class<?>> config = new HashMap<>() {{
            put("shorterService", IdentShorterService.class);
            put("linksRepo", InMemShortLinksRepo.class);
            put("shortenLinkService", DefaultShortenLinkService.class);
        }};
        /*
        TODO: Add bean for config with database connection and if we will add @Transactional we will create transaction
        Add repo which will work with JPA and entity manager
        Impl transaction support
         */
        BeanFactory context = new JavaConfAppContext(config);
        ShorterService shorterService = context.getBean("shorterService");
        ShortLinksRepo inMemShortLinksRepo = context.getBean("linksRepo");
        // ShortenLinkService sls = context.getBean("shortenLinkService");

        ShortenLinkService shortenLinkService = new DefaultShortenLinkService(inMemShortLinksRepo, shorterService);
        Link shortLink = shortenLinkService.shortLink(linkTo(url));
        System.out.println("Short link: " + shortLink.link());

        Optional<Link> fullLink = shortenLinkService.fullLink(shortLink);
        System.out.println("Full link: " + fullLink.get().link());

    }

}
