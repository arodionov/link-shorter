package shorter.service;

import ioc.annotations.Benchmark;
import shorter.model.Link;
import shorter.repo.ShortLinksRepo;

import java.util.Optional;

import static shorter.model.Link.HTTPLinkTo;

public class DefaultShortenLinkService implements ShortenLinkService {

	private final ShortLinksRepo shortLinksRepo;
	private final ShorterService shorterService;

	public DefaultShortenLinkService(ShorterService shorterService, ShortLinksRepo shortLinksRepo) {
		this.shorterService = shorterService;
		this.shortLinksRepo = shortLinksRepo;
	}

	@Override
	@Benchmark
	public Link shortLink(Link fullLink) {
		String fullPath = fullLink.getPath();
		String shortPath = shorterService.shorten(fullPath);
		shortLinksRepo.put(shortPath, fullPath);
		return HTTPLinkTo(shortPath);
	}

	@Override
    @Benchmark
	public Optional<Link> fullLink(Link shortLink) {
		String shortPath = shortLink.getPath();
		Optional<String> fullPath = shortLinksRepo.get(shortPath);
		return fullPath.map(Link::HTTPLinkTo);
	}
}
