package shorter.repo.impl;

import ioc.anotation.MyTransaction;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import shorter.model.LinkEntity;
import shorter.repo.ShortLinksRepo;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import java.util.Optional;

@Primary
@Repository
@MyTransaction
public class DbShortLinksRepo implements ShortLinksRepo {

    private EntityManager entityManager;

    @Override
    public Optional<String> get(String shortPath) {
        try {
            return Optional.of(entityManager
                    .createQuery("select l from LinkEntity l where l.shortPath = :shortPath", LinkEntity.class)
                    .setParameter("shortPath", shortPath)
                    .getSingleResult().getFullPath());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public void put(String shortPath, String fullPath) {
        LinkEntity link = LinkEntity.builder().shortPath(shortPath).fullPath(fullPath).build();
        entityManager.persist(link);
    }
}
