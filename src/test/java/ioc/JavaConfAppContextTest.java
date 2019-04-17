package ioc;

import org.junit.Test;
import shorter.service.IdentShorterService;
import shorter.service.ShorterService;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author Danil Kuznetsov (kuznetsov.danil.v@gmail.com)
 */
public class JavaConfAppContextTest {

    @Test
    public void createEmptyContext() {
        BeanFactory context = new JavaConfAppContext();
        assertNotNull(context);
    }

    @Test
    public void beanDefinitionsIsEmptyForEmptyContext() {
        BeanFactory context = new JavaConfAppContext();

        BeanDefinition[] names = context.getBeanDefinitionNames();

        assertEquals(0, names.length);
    }

    @Test
    public void beanDefinitionsNotEmptyForNotEmptyContext() {
        Map<String, Class<?>> config = new HashMap<String, Class<?>>() {{
            put("string", String.class);
        }};

        BeanFactory context = new JavaConfAppContext(config);

        BeanDefinition[] names = context.getBeanDefinitionNames();

        assertEquals(1, names.length);
    }

    @Test
    public void beanDefinitionByName() {
        Map<String, Class<?>> config = new HashMap<String, Class<?>>() {{
            put("shorterService", IdentShorterService.class);
        }};

        BeanFactory context = new JavaConfAppContext(config);

        BeanDefinition beanDefinition = context.getBeanDefinition("shorterService");

        assertEquals("shorterService", beanDefinition.getName());
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
    public void getBeanAreSame(){

        Map<String, Class<?>> config = new HashMap<String, Class<?>>() {{
            put("shorterService", IdentShorterService.class);
        }};

        BeanFactory context = new JavaConfAppContext(config);

        ShorterService firstShorterService = context.getBean("shorterService");
        ShorterService secondShorterService = context.getBean("shorterService");

        assertSame(firstShorterService, secondShorterService);
    }
}