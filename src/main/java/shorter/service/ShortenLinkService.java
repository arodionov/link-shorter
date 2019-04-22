package shorter.service;

import shorter.model.Link;
import shorter.model.ShortedLink;

import java.util.Optional;

public interface ShortenLinkService {

    ShortedLink shortLink(Link fullLink);

    Optional<Link> fullLink(String shortLink);

}
