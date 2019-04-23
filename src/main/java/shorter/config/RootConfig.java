package shorter.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import shorter.controller.HelloController;

@Configuration
@ComponentScan(basePackages = {"shorter.service","shorter.repo"})
public class RootConfig {

}
