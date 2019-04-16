package ioc.testData.dependency.circular.impl;

import ioc.testData.dependency.circular.CircularA;
import ioc.testData.dependency.circular.CircularC;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CircularCImpl implements CircularC {
    private final CircularA circularA;
}
