package shorter.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.BeanNameAware;
import shorter.model.Link;
import shorter.service.ShortenLinkService;
import web.MyController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@RequiredArgsConstructor
public class ShorterController implements MyController, BeanNameAware {

    private String beanName;
    private final ShortenLinkService shortenLinkService;

    @Override
    public void setBeanName(String s) {
        this.beanName = s;
    }

    @Override
    public void handleRequest(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String url = req.getRequestURL().toString();
        System.out.println("URL: " + url);
        Link link = Link.linkTo(url);
        try (PrintWriter writer = resp.getWriter()) {
            if (req.getRequestURI().contains("cut")) {
                Link shortenLink = shortenLinkService.shortLink(link);
                writer.println("short link: " + shortenLink.link());
            } else if (req.getRequestURI().contains("full")) {
                shortenLinkService.fullLink(link).ifPresentOrElse(
                        fullLink -> writer.println("Full link: " + fullLink.link()),
                        () -> writer.println("Link not found"));
            } else {
                writer.print("unresolved mapping");
            }
        }
    }
}