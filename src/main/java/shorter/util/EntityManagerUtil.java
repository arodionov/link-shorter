package shorter.util;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.persistence.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EntityManagerUtil {

	private EntityManagerFactory entityManagerFactory;

	public EntityManagerUtil(EntityManagerFactory entityManagerFactory) {
		this.entityManagerFactory = entityManagerFactory;
	}

	public void performInTransaction(Consumer<EntityManager> consumer) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		EntityTransaction transaction = entityManager.getTransaction();
		try {
			transaction.begin();
			consumer.accept(entityManager);
			transaction.commit();
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			entityManager.close();
		}
	}

	public <T> Optional<T> performInTransactionWithOptionalResult(
		Function<EntityManager, T> consumer) {
		try {
			return Optional.ofNullable(performInTransactionWithResult(consumer));
		} catch (Exception e) {
			return Optional.empty();
		}
	}

	public <T> T performInTransactionWithResult(Function<EntityManager, T> consumer) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		EntityTransaction transaction = entityManager.getTransaction();
		try {
			transaction.begin();
			T apply = consumer.apply(entityManager);
			transaction.commit();
			return apply;
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			entityManager.close();
		}
	}
}
