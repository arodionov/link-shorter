package ioc.testData.dependency.init.impl;

import ioc.anotation.PostConstructBean;
import ioc.testData.dependency.init.PostConstructAndInit;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class PostConstructAndInitImpl implements PostConstructAndInit {

    private List<String> res = new ArrayList<>();

    @PostConstructBean
    public void init() {
        res.add("PostConstructAndInit");
    }
}
