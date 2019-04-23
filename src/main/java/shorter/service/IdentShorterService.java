package shorter.service;

import java.util.concurrent.ThreadLocalRandom;
import org.springframework.stereotype.Service;

@Service
public class IdentShorterService implements ShorterService {

	@Override
	public String shorten(String path) {
		return path+"shorten";
	}
}
