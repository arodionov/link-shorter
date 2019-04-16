package ioc.testData.dependency.benchmark;

import ioc.anotation.Benchmark;

public interface BenchmarkBean {

    @Benchmark
    void methodWithBenchmark();

    void methodWithoutBenchmark();
}
