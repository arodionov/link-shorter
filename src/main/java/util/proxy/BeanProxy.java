package util.proxy;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import shorter.model.Link;
import shorter.service.ShortenLinkService;
import util.annotation.Benchmark;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@SuppressWarnings("all")
public class BeanProxy implements InvocationHandler, ShortenLinkService {

    private final Class<?> clazz;
    private final Map<String, BeanMethodsData> methodMap;

    public BeanProxy(Class<?> clazz) {
        this.clazz = clazz;
        this.methodMap = new HashMap<>();

        for (Method method : clazz.getMethods()) {
            this.methodMap.put(method.getName(),
                    new BeanMethodsData(method.getName(), method, method.isAnnotationPresent(Benchmark.class)));
        }

    }

    @Override
    public Object invoke(Object bean, Method method, Object[] args) {
        System.out.println("proxy handler invoked");
        System.out.println(this.methodMap);
        if (this.methodMap.get(method.getName()).isAnnotatedWithBenchmerk()) {

            double start = System.nanoTime();
            Object res = invokeMethod(bean, method, args);
            double end = System.nanoTime();
            System.out.println("execution time of '" + method + "' is: " + (end - start) + " ns");

            return res;
        } else {
            return invokeMethod(bean, method, args);
        }
    }

    private Object invokeMethod(Object bean, Method method, Object[] args) {
        try {
            return method.invoke(bean, args);
        } catch (Exception e) {
            throw new RuntimeException("Error occured while invocation", e);
        }
    }

    @Override
    public Link shortLink(Link fullLink) {
        System.out.println("proxies 'shortLink'");
        return null;
    }

    @Override
    public Optional<Link> fullLink(Link shortLink) {
        System.out.println("proxies 'fullLink'");
        return Optional.empty();
    }

    @Getter
    @ToString
    @AllArgsConstructor
    private static class BeanMethodsData {
        private String name;
        private Method method;
        private boolean isAnnotatedWithBenchmerk;
    }
}