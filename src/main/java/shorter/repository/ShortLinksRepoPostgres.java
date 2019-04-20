package shorter.repository;

import javax.persistence.EntityManagerFactory;
import java.util.Optional;

public class ShortLinksRepoPostgres implements ShortLinksRepo {

    private final EntityManagerFactory entityManagerFactory;

    public ShortLinksRepoPostgres(final EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public Optional<String> get(final String shortPath) {
        return Optional.empty();
    }

    @Override
    public void put(final String shortPath, final String fullPath) {

    }
}
