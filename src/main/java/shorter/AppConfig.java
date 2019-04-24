package shorter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import shorter.repository.ShortLinksRepo;
import shorter.repository.impl.InMemShortLinksRepo;
import shorter.service.ShortenLinkService;
import shorter.service.ShorterService;
import shorter.service.impl.DefaultShortenLinkService;
import shorter.service.impl.IdentShorterService;

@Configuration
public class AppConfig {

    //TODO: configuration for business logic - services, repos, components

    @Bean
    public ShorterService shorterService() {
        return new IdentShorterService();
    }

    @Bean
    public ShortLinksRepo shortLinksRepo() {
        return new InMemShortLinksRepo();
    }

    @Bean
    @Autowired
    public ShortenLinkService shortenLinkService(ShortLinksRepo shortLinksRepo, ShorterService shorterService) {
        return new DefaultShortenLinkService(shortLinksRepo, shorterService);
    }
}