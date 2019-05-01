package shorter;

import lombok.SneakyThrows;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import shorter.config.AppConfig;
import shorter.model.Link;
import shorter.service.ShortenLinkService;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.Optional;

import static shorter.model.Link.linkTo;

public class ShrorterApp {

    private EntityManager entityManager;

    @SneakyThrows
    public static void main(String[] args) {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(AppConfig.class);
        ShortenLinkService shortenLinkService = ctx.getBean(ShortenLinkService.class);

        String url = "https://www.facebook.com/groups/KyivKUG/";
        Link shortLink = shortenLinkService.shortLink(linkTo(url));
        System.out.println("Short link: " + shortLink.link());

        Optional<Link> fullLink = shortenLinkService.fullLink(shortLink);
        fullLink.ifPresent(link -> System.out.println("Full link: " + link.link()));

        ctx.close();
//        EntityManagerFactory shorter = Persistence.createEntityManagerFactory("shorter");
//        ShrorterApp shrorterApp = new ShrorterApp();
//        EntityManager entityManager = shorter.createEntityManager();
//
//        EntityManager entityManagerProxy = (EntityManager) Proxy.newProxyInstance(
//                EntityManager.class.getClassLoader(),
////                EntityManager.class.getInterfaces(),
//                new Class[]{EntityManager.class},
//                ((proxy, method, args1) -> method.invoke(entityManager, args1)));
//
//        Field field = shrorterApp.getClass().getDeclaredField("entityManager");
//        field.set(shrorterApp, entityManagerProxy);
    }
}
