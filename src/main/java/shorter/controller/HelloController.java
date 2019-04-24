package shorter.controller;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.BeanNameAware;
import web.MyController;

public class HelloController implements MyController, BeanNameAware {

    private String beanName;

    @Override
    public void handleRequest(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try (PrintWriter writer = resp.getWriter()) {
            writer.println("Hello from HelloController");
            writer.println("Bean: " + this.beanName);
            writer.flush();
        }
    }

    @Override
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }
}
