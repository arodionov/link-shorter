package shorter.repo;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.springframework.stereotype.Repository;
import shorter.model.Link;

@Repository
public class InMemShortLinksRepo implements ShortLinksRepo {

	private Set<Link> links = new HashSet<>();

	@Override
	public Optional<Link> getByShortLink(String shortLink) {
		return links.stream()
			.filter(link -> link.getShortLink().equals(shortLink))
			.findAny();
	}

	@Override
	public Link put(String fullPath, String shortLink) {
		Link link = new Link(fullPath, shortLink);
		links.add(link);
		return link;
	}

	@Override
	public Set<Link> getAll() {
		return links;
	}

}
