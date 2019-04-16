package ioc.testData.dependency.init.impl;

import ioc.testData.dependency.init.InitMethod;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class InitMethodImpl implements InitMethod {

    private List<String> res = new ArrayList<>();

    public void init() {
        res.add("InitMethod");
    }
}
