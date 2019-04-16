package shorter.service.impl;

import shorter.service.ShorterService;

public class IdentShorterService implements ShorterService {

    @Override
    public String shorten(String path) {
        return path;
    }
}