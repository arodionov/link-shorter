package shorter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import shorter.repository.ShortLinksRepo;
import shorter.repository.ShortLinksRepoPostgres;
import shorter.service.DefaultShortenLinkService;
import shorter.service.IdentShorterService;
import shorter.service.ShortenLinkService;
import shorter.service.ShorterService;

import javax.persistence.EntityManagerFactory;

import static javax.persistence.Persistence.createEntityManagerFactory;

@Configuration
public class AppConfig {

    @Bean
    public ShortenLinkService getShortenLinkService(ShortLinksRepo repo, ShorterService service) {
        return new DefaultShortenLinkService(repo, service);
    }

    @Bean
    public ShorterService getShorterService() {
        return new IdentShorterService();
    }

    @Bean
    @Autowired
    public ShortLinksRepo getShortLinksRepo(EntityManagerFactory entityManagerFactory) {
        return new ShortLinksRepoPostgres(entityManagerFactory);
    }

    @Bean
    public EntityManagerFactory getEntityManagerFactory() {
        return createEntityManagerFactory("ShorterEntityProperties");
    }

}
