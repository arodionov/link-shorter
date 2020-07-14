package shorter.controller;

import org.springframework.beans.factory.BeanNameAware;
import shorter.model.Link;
import shorter.service.ShortenLinkService;
import web.MyController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class FullController implements MyController, BeanNameAware {

    private final ShortenLinkService shortenLinkService;

    public FullController(ShortenLinkService shortenLinkService) {
        this.shortenLinkService = shortenLinkService;
    }

    String beanName;

    @Override
    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try (PrintWriter writer = response.getWriter()) {
            String link = request.getParameter("short");

            Link link1 = this.shortenLinkService.fullLink(new Link(link)).orElse(Link.linkTo(""));
            writer.println("full link : " + link);
            writer.println("Bean: " + beanName);
            writer.flush();
        }
    }

    @Override
    public void setBeanName(String s) {
        this.beanName = s;
    }

    public static void main(String[] args) {
        int fibonacci = fibonacci(6);
        System.out.println(fibonacci);
    }

    private static int fibonacci(int var) {
        int first = 1;
        int second = 1;
        int result = first + second; // 2 = 1 + 1
//        first = result;  // 2
//        result = first + second; // 3 = 2 + 1
//        second = result; // 3
//        result = first + second; // 5 = 2 + 3
//        first = result;  // 5
//        result = first + second; // 8 = 5 + 3

        while (first < var && second < var) {
            first = result;
            result = first + second;
            second = result;
            result = first + second;
        }
        return result;

    }
}
