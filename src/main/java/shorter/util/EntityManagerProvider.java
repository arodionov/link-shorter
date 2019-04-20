package shorter.util;

import javax.persistence.EntityManager;

public interface EntityManagerProvider {
    EntityManager getEntityManager();
}
