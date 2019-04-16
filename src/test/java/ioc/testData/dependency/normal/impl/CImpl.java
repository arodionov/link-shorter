package ioc.testData.dependency.normal.impl;

import ioc.anotation.PostConstructBean;
import ioc.testData.dependency.normal.A;
import ioc.testData.dependency.normal.B;
import ioc.testData.dependency.normal.C;
import lombok.Getter;

import java.util.Random;

@Getter
//@RequiredArgsConstructor
public class CImpl implements C {
    private final Long id = new Random().nextLong();
    private final A a;
    private final B b;

    public CImpl(A a, B b) {
        this.a = a;
        this.b = b;
    }

    @PostConstructBean
    public void someMethod() {
        System.out.println("Bean CImpl init");
    }
}