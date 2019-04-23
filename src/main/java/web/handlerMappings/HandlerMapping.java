package web.handlerMappings;

import javax.servlet.http.HttpServletRequest;
import org.springframework.context.ApplicationContext;
import web.MyController;

public abstract class HandlerMapping {
	protected ApplicationContext applicationContext;


	public HandlerMapping() {
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	public abstract MyController getController(HttpServletRequest httpServletRequest);


}
