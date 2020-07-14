package shorter.service;

import java.util.Optional;

import shorter.model.Link;
import util.annotation.Benchmark;

public interface ShortenLinkService {

	@Benchmark
	Link shortLink(Link fullLink);

	Optional<Link> fullLink(Link shortLink);

}
