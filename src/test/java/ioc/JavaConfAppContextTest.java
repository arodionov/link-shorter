package ioc;

import ioc.testData.dependency.benchmark.BenchmarkBean;
import ioc.testData.dependency.benchmark.impl.BenchmarkBeanImpl;
import ioc.testData.dependency.circular.impl.CircularAImpl;
import ioc.testData.dependency.circular.impl.CircularBImpl;
import ioc.testData.dependency.circular.impl.CircularCImpl;
import ioc.testData.dependency.init.InitMethod;
import ioc.testData.dependency.init.PostConstruct;
import ioc.testData.dependency.init.PostConstructAndInit;
import ioc.testData.dependency.init.impl.InitMethodImpl;
import ioc.testData.dependency.init.impl.PostConstructAndInitImpl;
import ioc.testData.dependency.init.impl.PostConstructImpl;
import ioc.testData.dependency.normal.A;
import ioc.testData.dependency.normal.B;
import ioc.testData.dependency.normal.C;
import ioc.testData.dependency.normal.D;
import ioc.testData.dependency.normal.impl.AImpl;
import ioc.testData.dependency.normal.impl.BImpl;
import ioc.testData.dependency.normal.impl.CImpl;
import ioc.testData.dependency.normal.impl.DImpl;
import org.junit.Test;
import shorter.service.IdentShorterService;
import shorter.service.ShorterService;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Map;

import static org.junit.Assert.*;

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
        Map<String, Class<?>> beansMap = Map.of("string", String.class);
        BeanFactory context = new JavaConfAppContext(beansMap);
        BeanDefinition[] names = context.getBeanDefinitionNames();
        assertEquals(1, names.length);
    }

    @Test
    public void beanDefinition() {
        Map<String, Class<?>> beansMap = Map.of("shorterService", IdentShorterService.class);
        BeanFactory context = new JavaConfAppContext(beansMap);
        BeanDefinition name = context.getBeanDefinition("shorterService");
        assertNotNull(name);
    }

    @Test
    public void getBeanWithoutDeps() {
        Map<String, Class<?>> beansMap = Map.of("shorterService", IdentShorterService.class);
        BeanFactory context = new JavaConfAppContext(beansMap);
        ShorterService shorterService = context.getBean("shorterService");
        assertNotNull(shorterService);
    }

    @Test
    public void dependenciesWithParams() {
        Map<String, Class<?>> beansMap = Map.of(
                "complexNameD", DImpl.class,
                "complexNameC", CImpl.class,
                "complexNameB", BImpl.class,
                "complexNameA", AImpl.class
        );
        JavaConfAppContext context = new JavaConfAppContext(beansMap);

        A a = context.getBean("complexNameA");
        assertNotNull(a);

        B b = context.getBean("complexNameB");
        assertNotNull(b);

        C c = context.getBean("complexNameC");
        assertNotNull(c);
        assertEquals(c.getA().getId(), a.getId());
        assertEquals(c.getB().getId(), b.getId());

        D d = context.getBean("complexNameD");
        assertNotNull(d);
        assertEquals(d.getA().getId(), a.getId());
        assertEquals(d.getB().getId(), b.getId());
        assertEquals(d.getC().getId(), c.getId());
    }

    @Test
    public void circularDependenciesRecognition() {
        Map<String, Class<?>> beansMap = Map.of(
                "circularA", CircularAImpl.class,
                "circularB", CircularBImpl.class,
                "circularC", CircularCImpl.class
        );
        try {
            JavaConfAppContext context = new JavaConfAppContext(beansMap);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            assertTrue(e.getMessage().contains("Circular dependency is found "));
        }
    }

    @Test
    public void postConstructTest() {
        Map<String, Class<?>> beansMap = Map.of(
                "initMethod", InitMethodImpl.class,
                "postConstruct", PostConstructImpl.class,
                "postConstructAndInit", PostConstructAndInitImpl.class
        );
        JavaConfAppContext context = new JavaConfAppContext(beansMap);

        InitMethod initMethod = context.getBean("initMethod");
        assertNotNull(initMethod);
        assertTrue(initMethod.getRes().contains("InitMethod"));

        PostConstruct postConstruct = context.getBean("postConstruct");
        assertNotNull(postConstruct);
        assertTrue(postConstruct.getRes().contains("PostConstruct"));

        PostConstructAndInit postConstructAndInit = context.getBean("postConstructAndInit");
        assertNotNull(postConstructAndInit);
        assertEquals(1, postConstructAndInit.getRes().size());
        assertTrue(postConstructAndInit.getRes().contains("PostConstructAndInit"));
    }

    @Test
    public void testBenchmark() {
        Map<String, Class<?>> beansMap = Map.of(
                "benchmarkBean", BenchmarkBeanImpl.class
        );
        JavaConfAppContext context = new JavaConfAppContext(beansMap);
        BenchmarkBean benchmarkBean = context.getBean("benchmarkBean");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        PrintStream old = System.out;
        System.setOut(ps);

        benchmarkBean.methodWithBenchmark();
        benchmarkBean.methodWithoutBenchmark();

        System.out.flush();
        System.setOut(old);

        String consoleOutput = baos.toString();
        assertTrue(consoleOutput.contains("methodWithBenchmark"));
        assertFalse(consoleOutput.contains("methodWithoutBenchmark"));
    }

}