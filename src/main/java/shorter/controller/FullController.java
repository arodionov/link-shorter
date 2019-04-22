package shorter.controller;

import org.springframework.beans.factory.BeanNameAware;
import shorter.model.Link;
import shorter.service.ShortenLinkService;
import web.MyController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;

import static shorter.util.LinkUtil.getPath;

public class FullController implements MyController, BeanNameAware {

    private final ShortenLinkService shortenLinkService;
    private String beanName;

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
            String s = request.getQueryString();
            String shortLink = s.substring(s.lastIndexOf('=') + 1);
            System.out.println("SL s: " + s);
            System.out.println("SL: " + shortLink);
            Optional<Link> link = shortenLinkService.fullLink(getPath(shortLink));
            writer.write(link.get().getLink());
            writer.flush();
        }
    }
}
