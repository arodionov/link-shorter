package shorter.util;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class EntityManagerProviderImpl implements EntityManagerProvider {

    private final EntityManagerFactory entityManagerFactory;

    public EntityManagerProviderImpl(final EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public EntityManager getEntityManager() {
        return entityManagerFactory.createEntityManager();
    }

}
