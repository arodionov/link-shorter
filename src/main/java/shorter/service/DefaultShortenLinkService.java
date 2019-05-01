package shorter.service;

import ioc.anotation.MyTransaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;
import shorter.model.Link;
import shorter.repo.ShortLinksRepo;

import java.util.Optional;

import static shorter.model.Link.HTTPLinkTo;

@Log
@Service
@MyTransaction
@RequiredArgsConstructor
public class DefaultShortenLinkService implements ShortenLinkService {

    private final ShortLinksRepo shortLinksRepo;
    private final ShorterService shorterService;


    @Override
    public Link shortLink(Link fullLink) {
        String fullPath = fullLink.getPath();
        String shortPath = shorterService.shorten(fullPath);
        shortLinksRepo.put(shortPath, fullPath);
        return HTTPLinkTo(shortPath);
    }

    @Override
    public Optional<Link> fullLink(Link shortLink) {
        String shortPath = shortLink.getPath().substring(shortLink.getPath().lastIndexOf("/") + 1);
        System.out.println("shortPath: " + shortPath);
        Optional<String> fullPath = shortLinksRepo.get(shortPath);
        return fullPath.map(Link::HTTPLinkTo);
    }
}
