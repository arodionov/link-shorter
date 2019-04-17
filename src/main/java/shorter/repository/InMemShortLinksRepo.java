package shorter.repository;

import ioc.annotations.Benchmark;
import ioc.annotations.PostConstructBean;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemShortLinksRepo implements ShortLinksRepo {

	private Map<String, String> links = new HashMap<>();

	@Override
	public Optional<String> get(String shortPath) {
		return Optional.ofNullable(links.get(shortPath));
	}

	@Override
	public void put(String shortPath, String fullPath) {
		links.put(shortPath, fullPath);
	}

	public void init() {
		System.out.println("Hello");
	}

	@PostConstructBean
	public void postConstruct() {
		System.out.println("World");
	}

	@Benchmark
	public void measuredMethod() {
        System.out.println("!");
    }

}
