package shorter.controller;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.annotation.Autowired;
import shorter.model.Link;
import shorter.service.ShortenLinkService;
import web.MyController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class ShorterController implements MyController, BeanNameAware {

    private final ShortenLinkService shortenLinkService;

    String beanName;

    public ShorterController(ShortenLinkService shortenLinkService) {
        this.shortenLinkService = shortenLinkService;
    }

    @Override
    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try (PrintWriter writer = response.getWriter()) {
            String link = request.getParameter("short");

            Link shortLink = this.shortenLinkService.shortLink(new Link(link));
            writer.println("short link : " + shortLink.getPath());

            writer.println("Bean: " + beanName);
            writer.flush();
        }
    }

    @Override
    public void setBeanName(String s) {
        this.beanName = s;
    }
}
