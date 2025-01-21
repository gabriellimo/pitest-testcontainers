package test.pitest_containers.whatever;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BookCustomRepositoryTest {

    @Autowired
    private BookCustomRepository repository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void countBooks() {

        String searchString = "Gabriel";

        Query nativeQuery = entityManager.createNativeQuery("SELECT COUNT(*) FROM book WHERE LOWER(name) LIKE '%"+searchString.toLowerCase()+"%' OR LOWER(author) LIKE '"+searchString.toLowerCase()+"'");
        long count = (long) nativeQuery.getSingleResult();

        BookFilter filter = mock(BookFilter.class);

        when(filter.searchString()).thenReturn(searchString);
        when(filter.isAvailable()).thenReturn(null);
        when(filter.publicationDate()).thenReturn(null);

        long repositoryCount = repository.countBooks(filter);

        assertEquals(count, repositoryCount);
    }
}