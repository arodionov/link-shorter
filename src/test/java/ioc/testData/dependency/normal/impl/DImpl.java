package ioc.testData.dependency.normal.impl;

import ioc.anotation.Benchmark;
import ioc.anotation.PostConstructBean;
import ioc.testData.dependency.normal.A;
import ioc.testData.dependency.normal.B;
import ioc.testData.dependency.normal.C;
import ioc.testData.dependency.normal.D;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class DImpl implements D {
    private final A a;
    private final B b;
    private final C c;

    @Benchmark
    @PostConstructBean
    public void init() {
        System.out.println("Bean DImpl init");
    }
}