package shorter.controller;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shorter.model.Link;
import shorter.service.ShortenLinkService;
import web.MyController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;

public class FullController implements MyController, BeanNameAware {

    private String beanName;

    private final ShortenLinkService shortenLinkService;

    public FullController(final ShortenLinkService shortenLinkService) {
        this.shortenLinkService = shortenLinkService;
    }

    @Override
    public void setBeanName(final String name) {
        this.beanName = name;
    }

    @Override
    public void handleRequest(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        try (PrintWriter writer = response.getWriter()) {
            String shortLink = request.getRequestURI().substring(request.getRequestURI().lastIndexOf("=") + 1);
            Optional<Link> link = shortenLinkService.fullLink(shortLink);
            writer.write(link.get().getLink());
            writer.flush();
        }
    }
}
