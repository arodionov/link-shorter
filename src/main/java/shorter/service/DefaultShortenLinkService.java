package shorter.service;

import java.util.Collection;
import java.util.Optional;
import org.springframework.stereotype.Service;
import shorter.model.Link;
import shorter.repo.ShortLinksRepo;

@Service
public class DefaultShortenLinkService implements ShortenLinkService {

	private final ShortLinksRepo shortLinksRepo;
	private final ShorterService shorterService;

	public DefaultShortenLinkService(ShortLinksRepo repo, ShorterService service) {
		shortLinksRepo = repo;
		shorterService = service;
	}

	@Override
	public Link shortLink(String fullLink) {
		String shortLink = shorterService.shorten(fullLink);
		shortLinksRepo.put(shortLink, fullLink);
		return new Link(fullLink, shortLink);
	}

	@Override
	public Optional<Link> fullLink(String shortLink) {
		return shortLinksRepo.getByShortLink(shortLink);
	}

	@Override
	public Collection<Link> allLinks() {
		return shortLinksRepo.getAll();
	}
}
