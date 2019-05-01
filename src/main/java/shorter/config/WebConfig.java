package shorter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import shorter.controller.HelloController;
import shorter.controller.ShorterController;
import shorter.service.ShortenLinkService;
import web.MyController;

@Configuration
public class WebConfig {

    @Bean
    public MyController hello() {
        return new HelloController();
    }

    @Bean
    public ShorterController shorter(ShortenLinkService shortenLinkService) {
        return new ShorterController(shortenLinkService);
    }
}
