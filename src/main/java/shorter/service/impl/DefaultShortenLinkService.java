package shorter.service.impl;

import shorter.model.Link;
import shorter.repository.ShortLinksRepo;
import shorter.service.ShortenLinkService;
import shorter.service.ShorterService;
import util.annotation.Benchmark;
import util.annotation.PostConstructBean;

import java.util.Optional;

import static shorter.model.Link.HTTPLinkTo;

public class DefaultShortenLinkService implements ShortenLinkService {

    private final ShortLinksRepo shortLinksRepo;
    private final ShorterService shorterService;

    public DefaultShortenLinkService(ShortLinksRepo shortLinksRepo, ShorterService shorterService) {
        this.shortLinksRepo = shortLinksRepo;
        this.shorterService = shorterService;
    }

    @PostConstructBean
    public void init() {
        System.out.println("init() : " + this.getClass().getName());
    }

    @Override
    @Benchmark
    public Link shortLink(Link fullLink) {
        System.out.println("in shortLink");
        String fullPath = fullLink.getPath();
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
