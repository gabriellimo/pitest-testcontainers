package test.pitest_containers.whatever;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import javax.sql.DataSource;
import org.hibernate.query.NativeQuery;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BookCustomRepositoryTest {


    @LocalServerPort
    private Integer port;

    @Autowired
    private BookCustomRepository repository;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private EntityManager entityManager;

    private static boolean initialized = false;

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:12.5"
    );

    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

    @BeforeEach
    void beforeEach() {
        if (!initialized) {
            ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
            populator.addScript(new ClassPathResource("/scripts/books.sql"));
            populator.execute(dataSource);
            initialized = true;
        }
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }
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