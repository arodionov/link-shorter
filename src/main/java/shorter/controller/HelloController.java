package shorter.controller;

import org.springframework.beans.factory.BeanNameAware;
import web.MyController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class HelloController implements MyController, BeanNameAware {

    private String beanName;

    @Override
    public void handleRequest(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try (PrintWriter writer = resp.getWriter()) {
            writer.print("Hello from HelloController\n");
            writer.print("Bean: " + beanName);
            writer.println();
        }
    }

    @Override
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }
}
