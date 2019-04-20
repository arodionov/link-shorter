package shorter.repository;

import ioc.annotations.Transactional;
import org.hibernate.Session;
import shorter.model.Link;
import shorter.model.ShortedLink;
import shorter.util.EntityManagerProvider;

import javax.persistence.EntityManager;
import java.util.Optional;

public class ShortLinksRepoPostgres implements ShortLinksRepo {

    private final EntityManagerProvider entityManagerProvider;

    public ShortLinksRepoPostgres(final EntityManagerProvider entityManagerProvider) {
        this.entityManagerProvider = entityManagerProvider;
    }

    @Override
    public Optional<String> get(final String shortPath) {
        EntityManager entityManager = entityManagerProvider.getEntityManager();
        ShortedLink shortedLink =
                entityManager.unwrap(Session.class).bySimpleNaturalId(ShortedLink.class).load(shortPath);

        if (shortedLink != null) {
            Link link = entityManager.find(Link.class, shortedLink.getLinkId());
            return Optional.ofNullable(link.getLink());
        } else {
            return Optional.empty();
        }
    }

    @Override
    @Transactional
    public void put(final String shortPath, final String fullPath) {
        EntityManager entityManager = entityManagerProvider.getEntityManager();
        entityManager.joinTransaction();
        Link link = new Link(fullPath);
        entityManager.persist(link);
        ShortedLink shortedLink = new ShortedLink(shortPath, link);
        entityManager.persist(shortedLink);
    }
}
