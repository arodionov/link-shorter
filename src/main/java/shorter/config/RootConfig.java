package shorter.config;

import javax.persistence.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@Configuration
@ComponentScan(basePackages = "shorter", excludeFilters = {
	@Filter(pattern = "shorter\\.controller.*", type = FilterType.REGEX),
})
public class RootConfig {

	@Bean
	public EntityManagerFactory entityManagerFactory() {
		return Persistence.createEntityManagerFactory("LinkShorterPersistence");
	}
}
