package shorter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import shorter.controller.FullController;
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

    //req examp: http://localhost:8080/shorter/short?full=http://jug.ua
    @Bean
    @Autowired
    public MyController shorter(ShortenLinkService shortenLinkService) {
        return new ShorterController(shortenLinkService);
    }

    //req examp: http://localhost:8080/shorter/full?short=http://asdsa.sh
    @Bean
    @Autowired
    public MyController full(ShortenLinkService shortenLinkService) {
        return new FullController(shortenLinkService);
    }

}
