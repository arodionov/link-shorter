package ioc.testData.dependency.normal.impl;

import ioc.testData.dependency.normal.A;
import lombok.Getter;

import java.util.Random;

@Getter
public class AImpl implements A {
    private final Long id = new Random().nextLong();

    @Override
    public void sleep() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}