package web;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class MyDispatcherServlet extends HttpServlet {

	private AnnotationConfigApplicationContext webContext;

	@Override
	public void init() {
		ApplicationContext rootApplicationContext = getRootApplicationContext();
		initWebContext(rootApplicationContext);
	}

	private void initWebContext(ApplicationContext rootApplicationContext) {
		Class<?> webConfigClass = getWebConfigClass();
		//We cant set config class in the constructor, bc we have dependency from parent
		//Singleton beans initialize and throw an Exception. (@Lazy fix this)
		webContext = new AnnotationConfigApplicationContext();
		webContext.setParent(rootApplicationContext);
		webContext.register(webConfigClass);
		webContext.refresh();
	}

	private ApplicationContext getRootApplicationContext() {
		return (ApplicationContext) getServletContext().getAttribute("root-config");
	}

	private Class<?> getWebConfigClass() {
		try {
			return Class.forName(getInitParameter("web-config"));
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException {
		String controllerName = getControllerName(req);
		MyController controller = (MyController) webContext.getBean(controllerName);
		controller.handleRequest(req, resp);
	}

	private String getControllerName(HttpServletRequest req) {
		String uri = req.getRequestURI();
		return uri.substring(uri.lastIndexOf("/") + 1);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException {

	}

}
