package shorter;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import shorter.model.Link;
import shorter.model.ShortedLink;
import shorter.service.ShortenLinkService;

import java.util.Optional;

import static shorter.model.Link.linkTo;

public class ShorterApp {

    public static void main(String[] args) {
        String url = "https://www.facebook.com/groups/KyivKUG/";
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        ShortenLinkService shortenLinkService = context.getBean(ShortenLinkService.class);

        ShortedLink shortLink = shortenLinkService.shortLink(linkTo(url));
        System.out.println("Short link: " + shortLink.link());

        Optional<Link> fullLink = shortenLinkService.fullLink(shortLink.link());
        System.out.println("Full link: " + fullLink.get().link());

    }

}
