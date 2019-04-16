package ioc.testData.dependency.normal;

import ioc.anotation.Benchmark;

public interface A {
    @Benchmark
    void sleep();

    Long getId();
}
