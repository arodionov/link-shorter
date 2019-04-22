package shorter;

import ioc.BeanFactory;
import ioc.JavaConfAppContext;
import shorter.model.Link;
import shorter.repository.ShortLinksRepo;
import shorter.repository.ShortLinksRepoPostgres;
import shorter.service.DefaultShortenLinkService;
import shorter.service.IdentShorterService;
import shorter.service.ShortenLinkService;
import shorter.service.ShorterService;
import shorter.util.EntityManagerProvider;
import shorter.util.EntityManagerProviderImpl;

import javax.persistence.EntityManagerFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static shorter.model.Link.linkTo;

public class ShorterApp {

    public static void main(String[] args) {
        String url = "https://www.facebook.com/groups/KyivKUG/";

        Map<String, Class<?>> config = new HashMap<>() {{
            put(EntityManagerFactory.class.getName(), EntityManagerFactory.class);
            put(ShortLinksRepo.class.getName(), ShortLinksRepoPostgres.class);
            put(EntityManagerProvider.class.getName(), EntityManagerProviderImpl.class);
            put(ShorterService.class.getName(), IdentShorterService.class);
            put(ShortenLinkService.class.getName(), DefaultShortenLinkService.class);
        }};
        BeanFactory context = new JavaConfAppContext(config);
        ShortenLinkService shortenLinkService = context.getBean(ShortenLinkService.class.getName());

        Link shortLink = shortenLinkService.shortLink(linkTo(url));
        System.out.println("Short link: " + shortLink.link());

        Optional<Link> fullLink = shortenLinkService.fullLink(shortLink);
        System.out.println("Full link: " + fullLink.get().link());

    }

}