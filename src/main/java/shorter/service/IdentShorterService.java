package shorter.service;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class IdentShorterService implements ShorterService {

    @Override
    public String shorten(String path) {
        return UUID.randomUUID().toString().substring(0, 6);
    }
}
