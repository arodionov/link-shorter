package web;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class MyContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        String rootContextConfigLocation = sce.getServletContext().getInitParameter("rootContextConfigLocation");
        AnnotationConfigApplicationContext rootContext =
            new AnnotationConfigApplicationContext(this.getClassByName(rootContextConfigLocation));

        sce.getServletContext().setAttribute("rootContext", rootContext);
    }

    private Class<?> getClassByName(String className) {
        try {
            return Class.forName(className);
        } catch (final ClassNotFoundException e) {
            throw new RuntimeException("Cannot create class by the given name");
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        final AnnotationConfigApplicationContext rootContext =
            (AnnotationConfigApplicationContext) sce.getServletContext().getAttribute("rootContext");
        rootContext.close();
    }
}
