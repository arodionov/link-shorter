package shorter.controller;


import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import shorter.model.Link;
import shorter.service.DefaultShortenLinkService;
import shorter.service.ShortenLinkService;
import web.MyController;

@Controller("defaultController")
public class LinkShorterController implements MyController {

	private ShortenLinkService shortenLinkService;

	public LinkShorterController(DefaultShortenLinkService shortenLinkService) {
		this.shortenLinkService = shortenLinkService;
	}

	@Override
	public void handleRequest(HttpServletRequest req, HttpServletResponse resp)
		throws IOException {
		try (PrintWriter writer = resp.getWriter()) {
			writer.println("default controller");
			String link = req.getRequestURI();
			Link shorten = shortenLinkService.shortLink(link);
			writer.println(shorten);
		}
	}


}
