package web;

import java.io.IOException;
import java.util.Enumeration;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import web.handlerMappings.HandlerMapping;

public class MyDispatcherServlet extends HttpServlet {

	private HandlerMapping handlerMapping;

	@Override
	public void init() {
		Enumeration<String> attributeNames = getServletContext().getAttributeNames();
		while (attributeNames.hasMoreElements()){
			System.out.println(attributeNames.nextElement());
		}
		ApplicationContext rootApplicationContext = getRootApplicationContext();
		ApplicationContext webContext = getWebContext(rootApplicationContext);
		handlerMapping = getHandlerMapping();
		System.out.println(handlerMapping);
		handlerMapping.setApplicationContext(webContext);
	}

	private ApplicationContext getWebContext(ApplicationContext rootApplicationContext) {
		AnnotationConfigApplicationContext webContext;
		Class<?> webConfigClass = getWebConfigClass();
		//We cant set config class in the constructor, bc we have dependency from parent
		//Singleton beans initialize and throw an Exception. (@Lazy fix this)
		webContext = new AnnotationConfigApplicationContext();
		webContext.setParent(rootApplicationContext);
		webContext.register(webConfigClass);
		webContext.refresh();
		return webContext;
	}

	private ApplicationContext getRootApplicationContext() {
		return (ApplicationContext) getServletContext().getAttribute("root-config");
	}

	private HandlerMapping getHandlerMapping() {
		return (HandlerMapping) getServletContext().getAttribute("handler-mapping");
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
		MyController controller = handlerMapping.getController(req);
		controller.handleRequest(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {

	}

}
