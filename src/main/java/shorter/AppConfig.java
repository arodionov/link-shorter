package shorter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import shorter.repo.InMemShortLinksRepo;
import shorter.repo.ShortLinksRepo;
import shorter.service.DefaultShortenLinkService;
import shorter.service.IdentShorterService;
import shorter.service.ShortenLinkService;
import shorter.service.ShorterService;

@Configuration
public class AppConfig {

    @Bean
    public ShorterService shorterService() {
        return new IdentShorterService();
    }

    @Bean
    public ShortLinksRepo shorterRepo() {
        return new InMemShortLinksRepo();
    }

    @Bean
    public ShortenLinkService shortenLinkService() {
        return new DefaultShortenLinkService(this.shorterService(), this.shorterRepo());
    }
}
