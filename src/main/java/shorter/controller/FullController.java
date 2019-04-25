package shorter.controller;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import shorter.model.Link;
import static shorter.model.Link.linkTo;
import shorter.service.ShortenLinkService;
import web.MyController;

public class FullController implements MyController {

    private final ShortenLinkService shortenLinkService;

    public FullController(final ShortenLinkService shortenLinkService) {
        this.shortenLinkService = shortenLinkService;
    }

    @Override
    public void handleRequest(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
        String[] params = req.getParameterValues("short");
        if(params.length == 0) {
            throw new RuntimeException("Required parameter short is not found");
        }

        Link fullLink = this.shortenLinkService
            .fullLink(linkTo(params[0]))
            .orElseThrow(() -> new RuntimeException("Full link is not found"));
        try (PrintWriter writer = resp.getWriter()) {
            writer.println("Full url is: " + fullLink.link());
        }
    }
}
