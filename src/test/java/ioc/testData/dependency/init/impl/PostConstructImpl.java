package ioc.testData.dependency.init.impl;

import ioc.testData.dependency.init.PostConstruct;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class PostConstructImpl implements PostConstruct {
    private List<String> res = new ArrayList<>();

    public void init() {
        res.add("PostConstruct");
    }
}
