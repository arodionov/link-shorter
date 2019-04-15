package ioc;

import org.junit.Test;

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
}