package shorter.controller;


import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import shorter.model.Link;
import shorter.service.DefaultShortenLinkService;
import shorter.service.IdentShorterService;
import web.MyController;

@Controller("hello")
public class HelloController implements MyController, BeanNameAware {

	private String beanName;
	private DefaultShortenLinkService defaultShortenLinkService;

	public HelloController(DefaultShortenLinkService defaultShortenLinkService) {
		this.defaultShortenLinkService = defaultShortenLinkService;
	}

	@Override
	public void handleRequest(HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException {
		try (PrintWriter writer = resp.getWriter()) {
			writer.println("HELLO From CONTROLLER + Container");
			String link = req.getRequestURI();
			Link shorten = defaultShortenLinkService.shortLink(link);
			writer.println(shorten);
		}
	}

	@Override
	public void setBeanName(String name) {
		this.beanName = name;
	}
}
