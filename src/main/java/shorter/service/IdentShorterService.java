package shorter.service;

import ioc.Benchmark;

public class IdentShorterService implements ShorterService {

    @Override
    @Benchmark
    public String shorten(String path) {
        return path;
    }

    public void init() {
        System.out.println("I'm in ShorterService init method!");
    }

}
