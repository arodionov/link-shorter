package shorter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import shorter.controller.HelloController;
import shorter.service.ShortenLinkService;
import shorter.controller.FullController;
import web.MyController;
import shorter.controller.ShortController;

@Configuration
public class WebConfig {

    private final ShortenLinkService shortenLinkService;

    public WebConfig(final ShortenLinkService shortenLinkService) {
        this.shortenLinkService = shortenLinkService;
    }

    @Bean
    public MyController hello() {
        return new HelloController();
    }

    @Bean
    public MyController full() {
        return new FullController(this.shortenLinkService);
    }

    @Bean
    public MyController shorter() {
        return new ShortController(this.shortenLinkService);
    }
}
