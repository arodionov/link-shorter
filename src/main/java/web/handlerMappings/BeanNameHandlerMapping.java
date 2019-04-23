package web.handlerMappings;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import web.MyController;

@Configuration
public class BeanNameHandlerMapping extends HandlerMapping {

	public BeanNameHandlerMapping() {
	}

	public BeanNameHandlerMapping(ApplicationContext webContext) {
		this.applicationContext = webContext;
	}

	@Override
	public MyController getController(HttpServletRequest req) {
		String controllerName = getControllerName(req);
		return findControllerInContext(controllerName);
	}

	private String getControllerName(HttpServletRequest req) {
		String uri = req.getRequestURI();
		System.out.println(uri);
		return uri.substring(uri.lastIndexOf("/") + 1);
	}

	private MyController findControllerInContext(String name) {
		try {
			return (MyController) applicationContext.getBean(name);
		} catch (BeansException e) {
			return (MyController) applicationContext.getBean("defaultController");
		}
	}


}
