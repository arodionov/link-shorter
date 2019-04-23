package shorter.repo;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.hibernate.Session;
import org.hibernate.annotations.QueryHints;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import shorter.model.Link;
import shorter.util.EntityManagerUtil;

@Repository
@Primary
public class DataBaseShortLinksRepo implements ShortLinksRepo {

	private EntityManagerUtil entityManagerUtil;

	public DataBaseShortLinksRepo(EntityManagerUtil entityManagerUtil) {
		this.entityManagerUtil = entityManagerUtil;
	}

	@Override
	public Optional<Link> getByShortLink(String shortLink) {
		return entityManagerUtil.performInTransactionWithOptionalResult(entityManager ->
			entityManager.unwrap(Session.class)
				.createQuery("select l from Link l where l.shortLink = :shortLink",
					Link.class)
				.setHint(QueryHints.READ_ONLY, true)
				.setParameter("shortLink", shortLink)
				.getSingleResult());
	}

	@Override
	public Link put(String fullLink, String shortLink) {
		Link link = new Link(fullLink, shortLink);
		entityManagerUtil.performInTransaction(entityManager -> entityManager.persist(link));
		return link;
	}

	@Override
	public List<Link> getAll() {
		return entityManagerUtil.performInTransactionWithResult(entityManager ->
			entityManager.unwrap(Session.class)
				.createQuery("select l from Link l",Link.class)
				.setHint(QueryHints.READ_ONLY, true)
				.getResultList());
	}

}
