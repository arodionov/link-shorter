package ioc.testData.dependency.circular.impl;

import ioc.testData.dependency.circular.CircularB;
import ioc.testData.dependency.circular.CircularC;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CircularBImpl implements CircularB {
    private final CircularC circularC;
}
