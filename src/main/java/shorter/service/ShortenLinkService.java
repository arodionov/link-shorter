package shorter.service;

import java.util.Collection;
import java.util.Optional;

import shorter.model.Link;

public interface ShortenLinkService {

	Link shortLink(String fullLink);

	Optional<Link> fullLink(String shortLink);

	Collection<Link> allLinks();
}
