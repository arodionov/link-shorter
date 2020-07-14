package ioc.service.impl;

import ioc.service.BeanFactory;
import org.junit.Ignore;
import org.junit.Test;
import shorter.model.Link;
import shorter.repository.impl.InMemShortLinksRepo;
import shorter.service.ShortenLinkService;
import shorter.service.ShorterService;
import shorter.service.impl.DefaultShortenLinkService;
import shorter.service.impl.IdentShorterService;
import shorter.service.test.FirstTestForCycleDepsService;
import shorter.service.test.SecondTestForCycleDepsService;
import util.BeanDefinition;
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
        Map<String, Class<?>> config = of("test", IdentShorterService.class);
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

    @Test(expected = CycleDependencyException.class)
    public void getBeanWithCycleDependencies() {
        Map<String, Class<?>> config = of(
                "firstTestForCycleDepsService", FirstTestForCycleDepsService.class,
                "secondTestForCycleDepsService", SecondTestForCycleDepsService.class);
        BeanFactory context = new JavaConfigAppContext(config);
    }

    @Test
    public void getBeanAreWithDependencies() {
        Map<String, Class<?>> config = of(
                "default", DefaultShortenLinkService.class,
                "linksRepo", InMemShortLinksRepo.class,
                "shorterService", IdentShorterService.class);
        BeanFactory context = new JavaConfigAppContext(config);

        ShortenLinkService def = context.getBean("default");
        System.out.println(def);
        def.shortLink(Link.linkTo("test"));
    }

    /**
     * HOMEWORK:
     * - finish with @Benchmark annotation
     * - @Transactional (EntityManagerBean (connect to db) - create, close tx, and save Links to database (with tx annotation)
     */
    public void hw() {

    }
}