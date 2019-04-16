package shorter.service;

import ioc.ApplicationBean;
import ioc.Inject;
import shorter.model.Link;
import shorter.repository.ShortLinksRepo;

import java.util.Optional;

import static shorter.model.Link.HTTPLinkTo;

@ApplicationBean
public class DefaultShortenLinkService implements ShortenLinkService {

    private final ShortLinksRepo shortLinksRepo;
    private final ShorterService shorterService;

    @Inject
    public DefaultShortenLinkService(ShortLinksRepo inMemShortLinksRepo, ShorterService identShorterService) {
        this.shortLinksRepo = inMemShortLinksRepo;
        this.shorterService = identShorterService;
    }

    @Override
    public Link shortLink(Link fullLink) {
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
