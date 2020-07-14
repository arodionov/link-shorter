package web;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import shorter.AppConfig;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MyDispatcherServlet extends HttpServlet {

    private AnnotationConfigApplicationContext webCtx;

    @Override
    public void init() throws ServletException {
        ApplicationContext parentContext = new AnnotationConfigApplicationContext(AppConfig.class);

        Class<?> webConfigClass = getWebConfigClass();
        webCtx = new AnnotationConfigApplicationContext();
        webCtx.setParent(parentContext);
        webCtx.register(webConfigClass);
        webCtx.refresh();

        //TODO: webCtx.setParent(parentContext);
        // should be loaded parent context with business logic

//        String contextConfigLocation = getInitParameter("contextConfigLocation");
//        try {
//            webCtx = new AnnotationConfigApplicationContext(Class.forName(contextConfigLocation));
//            webCtx.setParent();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
    }

    private Class<?> getWebConfigClass() {
        String contextConfigClass = getInitParameter("contextConfigLocation");

        try {
            return Class.forName(contextConfigClass);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        String controllerName = getControllerNameFromRequest(req);
        System.out.println("controller name : " + controllerName);
        MyController controller = (MyController) webCtx.getBean(controllerName);
        controller.handleRequest(req, resp);
    }

    private String getControllerNameFromRequest(HttpServletRequest req) {
        String requestURI = req.getRequestURI();
        return requestURI.substring(requestURI.lastIndexOf("/") + 1);
    }
    /**
     * add listener-class (impl ServletContextListener -> contextInitializer() context root ->
     * ShortedBeanService ..
     * Add bean creation to RootConfig
     *
     */
}