package et.gov.ethgov.search.application.query;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.search.adapter.out.persistence.InMemoryQueryRepository;
import et.gov.ethgov.search.domain.query.Query;
import et.gov.ethgov.search.domain.query.QueryStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class QueryApplicationTest {
    private InMemoryQueryRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryQueryRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateQueryUseCase(repository, clock);
        var activate = new ActivateQueryUseCase(repository, clock);
        var get = new GetQueryUseCase(repository);

        var id = create.execute(new CreateQueryUseCase.CreateQueryCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateQueryUseCase.ActivateQueryCommand(
                null, null, null, null, id.toString(), null));

        Query loaded = get.execute(id.toString());
        assertEquals(QueryStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
