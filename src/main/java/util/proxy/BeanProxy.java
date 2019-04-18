package util.proxy;

import shorter.model.Link;
import shorter.service.ShortenLinkService;
import util.annotation.Benchmark;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Optional;

public class BeanProxy implements InvocationHandler, ShortenLinkService {

    private final Class<?> clazz;

    public BeanProxy(Class<?> clazz) {
        this.clazz = clazz;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {

        if (method.isAnnotationPresent(Benchmark.class)) {
            System.out.println("Hello Proxy");
            try {
                for (Method method1 : this.getClass().getMethods()) {
                    if (method1.getName().equals(method.getName())) {
                    }
                }
                method.invoke(proxy);

            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("Buy Proxy");
        } else {
            try {
                method.invoke(proxy);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    @Override
    public Link shortLink(Link fullLink) {
        return null;
    }

    @Override
    public Optional<Link> fullLink(Link shortLink) {
        return Optional.empty();
    }
}
