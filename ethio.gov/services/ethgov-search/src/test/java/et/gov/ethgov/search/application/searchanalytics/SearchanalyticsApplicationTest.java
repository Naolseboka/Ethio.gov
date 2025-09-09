package et.gov.ethgov.search.application.searchanalytics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.search.adapter.out.persistence.InMemorySearchanalyticsRepository;
import et.gov.ethgov.search.domain.searchanalytics.Searchanalytics;
import et.gov.ethgov.search.domain.searchanalytics.SearchanalyticsStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SearchanalyticsApplicationTest {
    private InMemorySearchanalyticsRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemorySearchanalyticsRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateSearchanalyticsUseCase(repository, clock);
        var activate = new ActivateSearchanalyticsUseCase(repository, clock);
        var get = new GetSearchanalyticsUseCase(repository);

        var id = create.execute(new CreateSearchanalyticsUseCase.CreateSearchanalyticsCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateSearchanalyticsUseCase.ActivateSearchanalyticsCommand(
                null, null, null, null, id.toString(), null));

        Searchanalytics loaded = get.execute(id.toString());
        assertEquals(SearchanalyticsStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
