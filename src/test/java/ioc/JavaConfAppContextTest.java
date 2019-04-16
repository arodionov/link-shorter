package ioc;

import org.junit.Test;
import shorter.repo.InMemShortLinksRepo;
import shorter.service.DefaultShortenLinkService;
import shorter.service.IdentShorterService;
import shorter.service.ShortenLinkService;
import shorter.service.ShorterService;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

public class JavaConfAppContextTest {

    @Test
    public void createEmptyContext() {
        BeanFactory context = new JavaConfAppContext();
        assertNotNull(context);
    }

    @Test
    public void BDisEmptyForEmptyContext() {
        BeanFactory context = new JavaConfAppContext();
        BeanDefinition[] names = context.getBeanDefinitionNames();
        assertEquals(0, names.length);
    }

    @Test
    public void BDisNotEmptyForContext() {
        Map<String, Class<?>> config = new HashMap<String, Class<?>>() {{
            put("string", String.class);
        }};
        BeanFactory context = new JavaConfAppContext(config);
        BeanDefinition[] names = context.getBeanDefinitionNames();
        assertEquals(1, names.length);
    }

    @Test
    public void beanDefinition() {
        Map<String, Class<?>> config = new HashMap<String, Class<?>>() {{
            put("shorterService", IdentShorterService.class);
        }};
        BeanFactory context = new JavaConfAppContext(config);
        BeanDefinition name = context.getBeanDefinition("shorterService");
        assertNotNull(name);
    }

    @Test
    public void getBeanWithoutDeps() {
        Map<String, Class<?>> config = new HashMap<String, Class<?>>() {{
            put("shorterService", IdentShorterService.class);
        }};
        BeanFactory context = new JavaConfAppContext(config);
        ShorterService shorterService = context.getBean("shorterService");
        assertNotNull(shorterService);
    }

    @Test
    public void getBeanAreSame() {
        Map<String, Class<?>> config = new HashMap<String, Class<?>>() {{
            put("shorterService", IdentShorterService.class);
        }};
        BeanFactory context = new JavaConfAppContext(config);
        ShorterService shorterService1 = context.getBean("shorterService");
        ShorterService shorterService2 = context.getBean("shorterService");
        assertSame(shorterService1, shorterService2);
    }


    @Test
    public void getBeanWithDeps() {
        Map<String, Class<?>> config = new HashMap<String, Class<?>>() {{
            put("shorterService", IdentShorterService.class);
            put("shortLinksRepo", InMemShortLinksRepo.class);
            put("shortenLinkService", DefaultShortenLinkService.class);
        }};
        BeanFactory context = new JavaConfAppContext(config);
        ShortenLinkService shorterService = context.getBean("shortenLinkService");
        assertNotNull(shorterService);
    }

}