package ioc.testData.dependency.normal.impl;

import ioc.testData.dependency.normal.B;
import lombok.Getter;

import java.util.Random;

@Getter
public class BImpl implements B {
    private final Long id = new Random().nextLong();

    public void init() {
        System.out.println("Bean BImpl init");
    }
}