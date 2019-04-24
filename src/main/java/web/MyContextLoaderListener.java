package web;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class MyContextLoaderListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent event) {
        System.out.println("initialization");
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        System.out.println("destroying");
    }
}
