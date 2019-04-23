package shorter.service;

import static shorter.model.Link.HTTPLinkTo;

import java.util.Optional;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import shorter.model.Link;
import shorter.repo.ShortLinksRepo;

@Service
public class DefaultShortenLinkService implements ShortenLinkService {

	private final ShortLinksRepo shortLinksRepo;
	private final ShorterService shorterService;

	@Autowired
	public DefaultShortenLinkService(ShortLinksRepo repo, ShorterService service) {
		shortLinksRepo = repo;
		shorterService = service;
	}

	@Override
	public Link shortLink(String fullPath) {
		String shortPath = shorterService.shorten(fullPath);
		shortLinksRepo.put(shortPath, fullPath);
		return HTTPLinkTo(shortPath);
	}

	@Override
	public Optional<Link> fullLink(Link shortLink) {
		String shortPath = shortLink.getPath();
		Optional<String> fullPath = shortLinksRepo.get(shortPath);
		return fullPath.map(Link::HTTPLinkTo);
	}
}
