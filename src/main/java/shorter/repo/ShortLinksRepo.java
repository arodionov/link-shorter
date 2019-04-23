package shorter.repo;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import shorter.model.Link;

public interface ShortLinksRepo {

	Optional<Link> getByShortLink(String shortPath);

	Link put(String shortPath, String fullPath);

	Collection<Link> getAll();
}
