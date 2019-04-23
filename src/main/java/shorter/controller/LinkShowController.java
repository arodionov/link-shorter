package shorter.controller;


import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import shorter.service.DefaultShortenLinkService;
import shorter.service.ShortenLinkService;
import web.MyController;

@Controller("showController")
public class LinkShowController implements MyController {

	private ShortenLinkService shortenLinkService;

	public LinkShowController(DefaultShortenLinkService shortenLinkService) {
		this.shortenLinkService = shortenLinkService;
	}

	@Override
	public void handleRequest(HttpServletRequest req, HttpServletResponse resp)
		throws IOException {
		try (PrintWriter writer = resp.getWriter()) {
			shortenLinkService.allLinks()
				.forEach(writer::println);
		}
	}


}
