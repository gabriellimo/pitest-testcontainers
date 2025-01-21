package test.pitest_containers.whatever;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public class BookCustomRepository {

    private final EntityManager entityManager;

    public BookCustomRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public int countBooks(BookFilter filter) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Book> root = countQuery.from(Book.class);

        List<Predicate> predicates = buildPredicates(cb, root, filter);

        countQuery.select(cb.countDistinct(root)).where(predicates.toArray(new Predicate[0]));
        return entityManager.createQuery(countQuery).getSingleResult().intValue();
    }

    private List<Predicate> buildPredicates(CriteriaBuilder cb, Root<Book> root, BookFilter filter) {

        List<Predicate> predicates = new ArrayList<>();

        if (filter.publicationDate() != null) {
            predicates.add(cb.equal(root.get("publicationDate"), filter.publicationDate()));
        }

        if (filter.searchString() != null && !filter.searchString().isEmpty()) {
            String searchString = "%" + filter.searchString().toLowerCase() + "%";
            Predicate namePredicate = cb.like(cb.lower(root.get("name")), searchString);
            Predicate authorPredicate = cb.like(cb.lower(root.get("author")), searchString);
            predicates.add(cb.or(namePredicate, authorPredicate));
        }

        if (filter.isAvailable() != null) {
            predicates.add(cb.equal(root.get("isAvailable"), filter.isAvailable()));
        }

        return predicates;
    }

}
