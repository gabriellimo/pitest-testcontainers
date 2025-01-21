package test.pitest_containers.whatever;

import java.time.LocalDate;

public record BookFilter(
        String searchString,
        LocalDate publicationDate,
        Boolean isAvailable
){}
