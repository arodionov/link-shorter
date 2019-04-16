package ioc.service.impl;

import ioc.service.BeanDefinition;
import ioc.service.BeanFactory;
import org.junit.Test;
import shorter.service.ShortenLinkService;
import shorter.service.ShorterService;
import shorter.service.impl.IdentShorterService;

import java.util.Map;

import static org.junit.Assert.*;

public class JavaConfigAppContextTest {

    @Test
    public void createEmptyContext() {
        BeanFactory context = new JavaConfigAppContext();
        assertNotNull(context);
    }

    @Test
    public void BDisEmptyForEmpty() {
        BeanFactory context = new JavaConfigAppContext();
        BeanDefinition[] names = context.getBeanDefinitionNames();
        assertEquals(0, names.length);
    }

    @Test
    public void BDisNotEmptyForContext() {
        Map<String, Class<?>> config = Map.of("String", String.class);
        BeanFactory context = new JavaConfigAppContext(config);
        BeanDefinition[] names = context.getBeanDefinitionNames();
        assertEquals(1, names.length);
    }


    @Test
    public void beanDefinition() {
        Map<String, Class<?>> config = Map.of("shorterService", IdentShorterService.class);
        BeanFactory context = new JavaConfigAppContext(config);

        BeanDefinition name = context.getBeanDefinition("shorterService");
        assertNotNull(name);
    }

    @Test
    public void getBeanWithoutDeps() {
        Map<String, Class<?>> config = Map.of("shorterService", IdentShorterService.class);
        BeanFactory context = new JavaConfigAppContext(config);

        ShorterService shorterService = context.getBean("shorterService");
        assertNotNull(shorterService);
    }

    @Test
    public void getBeanAreTheSame() {
        Map<String, Class<?>> config = Map.of("shorterService", IdentShorterService.class);
        BeanFactory context = new JavaConfigAppContext(config);

        ShorterService shorterService = context.getBean("shorterService");
        ShorterService shorterService1 = context.getBean("shorterService");
        assertSame(shorterService, shorterService1);
    }
}