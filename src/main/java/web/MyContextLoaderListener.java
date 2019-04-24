package web;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import shorter.config.RootConfig;
import web.handlerMappings.BeanNameHandlerMapping;

public class MyContextLoaderListener implements
	ServletContextListener {

	private AnnotationConfigApplicationContext rootConfig;

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		System.out.println("Context is ON");
		rootConfig = new AnnotationConfigApplicationContext(
			RootConfig.class);
		sce.getServletContext().setAttribute("root-config", rootConfig);

		sce.getServletContext().setAttribute("handler-mapping", new BeanNameHandlerMapping());
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		rootConfig.close();
		System.out.println("Context is Off");
	}
}
