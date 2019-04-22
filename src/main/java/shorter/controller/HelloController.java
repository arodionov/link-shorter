package shorter.controller;

import web.MyController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class HelloController implements MyController {
    @Override
    public void handleRequest(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        try(PrintWriter writer = response.getWriter()) {
            writer.print("Hello from HelloController");
            writer.flush();
        }
    }
}
