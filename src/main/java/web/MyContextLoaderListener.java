package web;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import shorter.AppConfig;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class MyContextLoaderListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent event) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);
        event.getServletContext().setAttribute(AppConfig.class.getName(), applicationContext);
        System.out.println("initialization");
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        System.out.println("destroying");
    }
}
