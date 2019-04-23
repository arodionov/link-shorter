package shorter.service;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
public class HashCodeShorterService implements ShorterService {

	@Override
	public String shorten(String path) {
		return "http://"+ path.hashCode();
	}
}
