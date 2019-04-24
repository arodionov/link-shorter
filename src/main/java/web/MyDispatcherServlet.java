package web;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MyDispatcherServlet extends HttpServlet {

    private AnnotationConfigApplicationContext webCtx;

    @Override
    public void init()  throws ServletException {
        Class<?> webConfigClass = getWebConfigClass();
        webCtx = new AnnotationConfigApplicationContext(webConfigClass);

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
        String contextConfigClass =
                getInitParameter("contextConfigLocation");

        try {
            return Class.forName(contextConfigClass);
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        String controllerName = getControllerNameFromRequest(req);
        System.out.println(controllerName);
        MyController controller =
                (MyController) webCtx.getBean(controllerName);
        controller.handleRequest(req, resp);

//        String requestURL = req.getRequestURI();

//        String substring = requestURL.substring(requestURL.lastIndexOf("/") + 1);

//        String controllerName = "hello";
//        MyController controller = (MyController) webCtx.getBean(substring);
//        MyController controller = new HelloController();
//        controller.handleRequest(req, resp);
    }

    private String getControllerNameFromRequest(HttpServletRequest req) {
        String requestURI = req.getRequestURI();
        String name = requestURI.substring(requestURI.lastIndexOf("/") + 1);
        return name;
    }
    /**
     * add listener-class (impl ServletContextListener -> contextInitializer() context root ->
     * ShortedBeanService ..
     * Add bean creation to RootConfig
     *
     */
}