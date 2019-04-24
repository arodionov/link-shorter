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
    public void init() throws ServletException {
        String contextConfigLocation = getInitParameter("contextConfigLocation");
        try {
            webCtx = new AnnotationConfigApplicationContext(Class.forName(contextConfigLocation));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String controllerName = getControllerNameFromRequest(req);
        MyController myController = (MyController) webCtx.getBean(controllerName);
        myController.handleRequest(req,resp);
    }

    private String getControllerNameFromRequest(HttpServletRequest req) {
        String requestURI = req.getRequestURI();
        return requestURI.substring(requestURI.lastIndexOf("/") + 1);
    }
}
