package web;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.util.StreamUtils;
import shorter.WebConfig;
import shorter.controller.HelloController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.stream.Stream;

public class MyDispatcherServlet extends HttpServlet {

    private AnnotationConfigApplicationContext webCtx;

    @Override
    public void init() {
        String contextConfigLocation = getInitParameter("contextConfigLocation");
        try {
            webCtx = new AnnotationConfigApplicationContext(Class.forName(contextConfigLocation));
//            webCtx.setParent();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        String requestURL = req.getRequestURI();

        String substring = requestURL.substring(requestURL.lastIndexOf("/") + 1);

//        String controllerName = "hello";
        MyController controller = (MyController) webCtx.getBean(substring);
//        MyController controller = new HelloController();
        controller.handleRequest(req, resp);
    }

    /**
     * add listener-class (impl ServletContextListener -> contextInitializer() context root ->
     * ShortedBeanService ..
     * Add bean creation to RootConfig
     *
     */
}