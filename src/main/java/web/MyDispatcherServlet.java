package web;

import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Log
public class MyDispatcherServlet extends HttpServlet {

    private AnnotationConfigApplicationContext webCtx;

    @Override
    @SneakyThrows
    public void init() {
        String parentContextKey = getServletContext().getInitParameter("parentContextKey");
        AnnotationConfigApplicationContext appCtx = new AnnotationConfigApplicationContext(Class.forName(parentContextKey));

        String contextConfigLocation = getInitParameter("contextConfigLocation");
        webCtx = new AnnotationConfigApplicationContext();
        webCtx.setParent(appCtx);
        webCtx.register(Class.forName(contextConfigLocation));
        webCtx.refresh();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String controllerName = getControllerNameFromRequest(req);
        MyController controller = (MyController) webCtx.getBean(controllerName);
        controller.handleRequest(req, resp);
    }

    private String getControllerNameFromRequest(HttpServletRequest req) {
        System.out.println(req.getRequestURI());
        return req.getRequestURI().split("/")[1];
    }
}
