package shorter.repository;

import org.hibernate.Session;
import shorter.model.Link;
import shorter.model.ShortedLink;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.Optional;

public class ShortLinksRepoPostgres implements ShortLinksRepo {

    private final EntityManagerFactory entityManagerFactory;

    public ShortLinksRepoPostgres(final EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public Optional<String> get(final String shortPath) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
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
    public void put(final String shortPath, final String fullPath) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        Link link = new Link(fullPath);
        entityManager.persist(link);
        ShortedLink shortedLink = new ShortedLink(shortPath, link);
        entityManager.persist(shortedLink);
        entityManager.getTransaction().commit();
    }
}
