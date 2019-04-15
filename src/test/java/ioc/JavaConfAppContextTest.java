package ioc;

import org.junit.Test;
import shorter.service.IdentShorterService;

import java.util.Map;

import static org.junit.Assert.*;

public class JavaConfAppContextTest {

    @Test
    public void createEmptyContext() {
        BeanFactory context = new JavaConfAppContext();
        assertNotNull(context);
    }

    @Test
    public void bdIsEmptyForEmptyContext() {
        BeanFactory context = new JavaConfAppContext();

        assertEquals(0, context.getBeanDefinitionNames().length);
    }

    @Test
    public void beanDefinition() {
        Map<String, Class<?>> map = Map.of("shorterService", IdentShorterService.class);
        BeanFactory context = new JavaConfAppContext(map);

        assertNotNull(context.getBeanDefinition("shorterService"));
    }

    @Test
    public void getBean() {
        Map<String, Class<?>> map = Map.of("shorterService", IdentShorterService.class);
        BeanFactory context = new JavaConfAppContext(map);

        assertNotNull(context.getBean("shorterService"));
    }

    @Test
    public void getBeanShouldReturnSameBeanEachTime() {
        Map<String, Class<?>> map = Map.of("shorterService", IdentShorterService.class);
        BeanFactory context = new JavaConfAppContext(map);

        assertSame(context.getBean("shorterService"), context.getBean("shorterService"));
    }


}