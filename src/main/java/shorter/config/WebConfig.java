package shorter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import shorter.controller.HelloController;
import web.MyController;

@Configuration
@ComponentScan(basePackages = "shorter.controller")
public class WebConfig {


}
