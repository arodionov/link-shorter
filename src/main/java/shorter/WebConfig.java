package shorter;

import org.springframework.beans.factory.annotation.Autowired;
import shorter.controller.FullController;
import shorter.controller.HelloController;
import shorter.controller.ShorterController;
import shorter.service.ShortenLinkService;
import web.MyController;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebConfig {

	@Bean
	public MyController hello() {
		return new HelloController();
	}

	//req examp: http://localhost:8080/shorter/shorter?full=http://jug.ua
	@Bean
	@Autowired
	public MyController shorter(ShortenLinkService shortenLinkService){
		return new ShorterController(shortenLinkService);
	}

	//req examp: http://localhost:8080/shorter/full?short=http://asdsa.sh
	@Bean
	public MyController full(){
		return new FullController();
	}

	//TODO: create ShorterController bean to make short link from full
	//TODO: create FullController bean to make full link from short
}
