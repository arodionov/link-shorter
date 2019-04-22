package shorter;

import shorter.controller.HelloController;
import web.MyController;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebConfig {

	@Bean
	public MyController hello() {
		return new HelloController();
	}

	//TODO: create ShorterController bean to make short link from full
	//req examp: http://localhost:8080/shorter/short?full=http://jug.ua

	//TODO: create FullController bean to make full link from short
	//req examp: http://localhost:8080/shorter/full?short=http://asdsa.sh
}
