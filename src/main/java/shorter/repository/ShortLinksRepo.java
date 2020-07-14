package shorter.repository;

import java.util.Optional;

public interface ShortLinksRepo {

	Optional<String> get(String shortPath);

	void put(String shortPath, String fullPath);
}
