package web;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class MyContextLoaderListener implements ServletContextListener {

    @Override
    public void contextInitialized(final ServletContextEvent servletContextEvent) {

    }

    @Override
    public void contextDestroyed(final ServletContextEvent servletContextEvent) {

    }
}
