package shorter.config;

import javax.persistence.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;

@Configuration
@ComponentScan(basePackages = "shorter", excludeFilters = {
	@Filter(Controller.class),
	//Configuration is annotated with Component LOLOLOLOLOLOLOL
	@Filter(Configuration.class)
})
public class RootConfig {

	@Bean
	public EntityManagerFactory entityManagerFactory() {
		return Persistence.createEntityManagerFactory("LinkShorterPersistence");
	}
}
