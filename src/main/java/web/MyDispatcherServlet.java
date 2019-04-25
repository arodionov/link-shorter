package web;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class MyDispatcherServlet extends HttpServlet {

    private AnnotationConfigApplicationContext webCtx;

    @Override
    public void init() throws ServletException {
        final String contextConfigLocation = this.getInitParameter("contextConfigLocation");
        this.webCtx = new AnnotationConfigApplicationContext();

        final AnnotationConfigApplicationContext rootContext =
            (AnnotationConfigApplicationContext) this.getServletContext().getAttribute("rootContext");

        this.webCtx.setParent(rootContext);
        this.webCtx.register(this.getClassByName(contextConfigLocation));
        this.webCtx.refresh();
    }

    private Class<?> getClassByName(final String className) {
        try {
            return Class.forName(className);
        } catch (final ClassNotFoundException e) {
            throw new RuntimeException("Cannot create class by the given name");
        }
    }

    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        final String controllerName = this.getControllerNameFromRequest(req);
        final MyController myController = (MyController) this.webCtx.getBean(controllerName);
        myController.handleRequest(req, resp);
    }

    private String getControllerNameFromRequest(final HttpServletRequest req) {
        final String requestURI = req.getRequestURI();
        return requestURI.substring(requestURI.lastIndexOf("/") + 1);
    }
}
