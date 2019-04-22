package web;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import shorter.AppConfig;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class MyContextLoaderListener implements ServletContextListener {

    @Override
    public void contextInitialized(final ServletContextEvent servletContextEvent) {
        AnnotationConfigApplicationContext applicationContext =
                new AnnotationConfigApplicationContext(AppConfig.class);
        servletContextEvent.getServletContext().setAttribute(AppConfig.class.getName(),
                applicationContext);
    }

    @Override
    public void contextDestroyed(final ServletContextEvent servletContextEvent) {

    }
}
