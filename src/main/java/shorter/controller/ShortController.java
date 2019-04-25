package shorter.controller;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import shorter.model.Link;
import shorter.service.ShortenLinkService;
import web.MyController;

public class ShortController implements MyController {

    private final ShortenLinkService shortenLinkService;

    public ShortController(final ShortenLinkService shortenLinkService) {
        this.shortenLinkService = shortenLinkService;
    }

    @Override
    public void handleRequest(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
        String[] params = req.getParameterValues("full");

        if(params.length == 0) {
            throw new RuntimeException("Required parameter full is not found");
        }

        Link shortLink = this.shortenLinkService.shortLink(Link.linkTo(params[0])) ;
        try (PrintWriter writer = resp.getWriter()) {
            writer.println("Short url is: " + shortLink.link());
        }
    }
}
