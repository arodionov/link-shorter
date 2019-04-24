package web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface MyController {

    void handleRequest(HttpServletRequest request,
                       HttpServletResponse response) throws IOException;
}