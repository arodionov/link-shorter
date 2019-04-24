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
    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try (PrintWriter writer = response.getWriter()) {
            writer.println("Hello from HelloController");
            writer.println("Bean: " + beanName);
            writer.flush();
        }
    }

    @Override
    public void setBeanName(String name) {
        this.beanName = name;
    }
}
