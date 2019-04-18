package ioc.service.impl;

import shorter.model.Link;
import shorter.service.ShortenLinkService;
import shorter.service.impl.DefaultShortenLinkService;
import shorter.service.test.FirstTestForCycleDepsService;
import util.BeanDefinition;
import ioc.service.BeanFactory;
import org.junit.Test;
import shorter.service.ShorterService;
import shorter.service.impl.IdentShorterService;
import util.exception.CycleDependencyException;

import java.util.Map;

import static java.util.Map.of;
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
        Map<String, Class<?>> config = of("String", String.class);
        BeanFactory context = new JavaConfigAppContext(config);

        BeanDefinition[] names = context.getBeanDefinitionNames();

        assertEquals(1, names.length);
    }

    @Test
    public void beanDefinition() {
        Map<String, Class<?>> config = of("shorterService", IdentShorterService.class);
        BeanFactory context = new JavaConfigAppContext(config);

        BeanDefinition name = context.getBeanDefinition("shorterService");

        assertNotNull(name);
    }

    @Test
    public void getBeanWithoutDeps() {
        Map<String, Class<?>> config = of("shorterService", IdentShorterService.class);
        BeanFactory context = new JavaConfigAppContext(config);

        ShorterService shorterService = context.getBean("shorterService");

        assertNotNull(shorterService);
    }

    @Test
    public void getBeanAreTheSame() {
        Map<String, Class<?>> config = of("shorterService", IdentShorterService.class);
        BeanFactory context = new JavaConfigAppContext(config);

        ShorterService shorterService = context.getBean("shorterService");
        ShorterService shorterService1 = context.getBean("shorterService");

        assertSame(shorterService, shorterService1);
    }

    @Test
    public void getBeanWithCycleDependencies() {
        Map<String, Class<?>> config = of("test", FirstTestForCycleDepsService.class);
        BeanFactory context = new JavaConfigAppContext(config);
        try {
            context.getBean("test");
        } catch (Exception e) {
            String message = e.getMessage();
            assertEquals("There are cycle dependencies in your configuration.", message);
            assertEquals(CycleDependencyException.class, e.getClass());
        }
    }

    @Test
    public void getBeanAreWithDependencies() {
        Map<String, Class<?>> config = of("def", DefaultShortenLinkService.class);
        BeanFactory context = new JavaConfigAppContext(config);

        ShortenLinkService def = context.getBean("def");
        def.fullLink(Link.linkTo("test"));
    }

    /**
     * HOMEWORK:
     * - finish with @Benchmark annotation
     * - @Transactional (EntityManagerBean (connect to db) - create, close tx, and save Links to database (with tx annotation)
     */

    public void hw() {

    }
}