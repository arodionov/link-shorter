package ioc;

import org.junit.Test;
import shorter.service.IdentShorterService;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class JavaConfAppContextTest {

    @Test
    public void createEmptyContext() {
        BeanFactory context = new JavaConfAppContext();
        assertNotNull(context);
    }

    @Test
    public void dbIsEmptyForEmptyContext() {
        BeanFactory context = new JavaConfAppContext();
        BeanDefinition[] names = context.getBeanDefinitionNames();
        assertEquals(0, names.length);
    }

    @Test
    public void dbIsNotEmptyForEmptyContext() {
        Map<String, Class<?>> config = Map.of("BeanName", String.class);
        BeanFactory context = new JavaConfAppContext(config);
        BeanDefinition[] names = context.getBeanDefinitionNames();
        assertEquals(1, names.length);
    }

    @Test
    public void beanDefinition() {
        Map<String, Class<?>> config = Map.of("shorterService", IdentShorterService.class);
        BeanFactory context = new JavaConfAppContext(config);
        BeanDefinition definition = context.getBeanDefinition("shorterService");
        assertNotNull(definition);
    }
}