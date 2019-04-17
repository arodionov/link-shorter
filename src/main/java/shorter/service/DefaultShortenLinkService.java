package shorter.service;

import ioc.annotations.Benchmark;
import ioc.annotations.PostConstructBean;
import shorter.model.Link;
import shorter.repository.ShortLinksRepo;

import java.util.Optional;

import static shorter.model.Link.HTTPLinkTo;

public class DefaultShortenLinkService implements ShortenLinkService {

    private final ShortLinksRepo shortLinksRepo;
    private final ShorterService shorterService;

    public DefaultShortenLinkService(final ShortLinksRepo shortLinksRepo, final ShorterService shorterService) {
        this.shortLinksRepo = shortLinksRepo;
        this.shorterService = shorterService;
    }

    @Override
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

    public void init() {
        System.out.println("DefaultShortenLinkService init method");
    }

    @PostConstructBean
    public void postConstruct() {
        System.out.println("DefaultShortenLinkService post construct method");
    }

}
