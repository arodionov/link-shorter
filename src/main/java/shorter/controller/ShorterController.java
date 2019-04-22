package shorter.controller;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shorter.model.Link;
import shorter.model.ShortedLink;
import shorter.service.ShortenLinkService;
import web.MyController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class ShorterController implements MyController, BeanNameAware {

    private String beanName;

    private final ShortenLinkService shortenLinkService;

    public ShorterController(final ShortenLinkService shortenLinkService) {
        this.shortenLinkService = shortenLinkService;
    }

    @Override
    public void setBeanName(final String name) {
        this.beanName = name;
    }

    @Override
    public void handleRequest(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        try(PrintWriter writer = response.getWriter()) {
            String longLink = request.getRequestURI().substring(request.getRequestURI().lastIndexOf("=") + 1);
            Link link = new Link(longLink);
            ShortedLink shortedLink = shortenLinkService.shortLink(link);
            writer.println(shortedLink.link());
            writer.flush();
        }
    }
}
