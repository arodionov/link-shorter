package shorter.repository.impl;

import shorter.repository.ShortLinksRepo;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemShortLinksRepo implements ShortLinksRepo {

    private Map<String, String> links = new HashMap<>();

    public void init() {
        System.out.println("init() : " + this.getClass().getName());
    }

    @Override
    public Optional<String> get(String shortPath) {
        return Optional.ofNullable(links.get(shortPath));
    }

    @Override
    public void put(String shortPath, String fullPath) {
        links.put(shortPath, fullPath);
    }

}
