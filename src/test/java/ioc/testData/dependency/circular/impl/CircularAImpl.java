package ioc.testData.dependency.circular.impl;

import ioc.testData.dependency.circular.CircularA;
import ioc.testData.dependency.circular.CircularB;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CircularAImpl implements CircularA {
    private final CircularB circularB;
}
