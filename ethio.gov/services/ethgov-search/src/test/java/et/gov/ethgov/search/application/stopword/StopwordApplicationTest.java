package et.gov.ethgov.search.application.stopword;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.search.adapter.out.persistence.InMemoryStopwordRepository;
import et.gov.ethgov.search.domain.stopword.Stopword;
import et.gov.ethgov.search.domain.stopword.StopwordStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StopwordApplicationTest {
    private InMemoryStopwordRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryStopwordRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateStopwordUseCase(repository, clock);
        var activate = new ActivateStopwordUseCase(repository, clock);
        var get = new GetStopwordUseCase(repository);

        var id = create.execute(new CreateStopwordUseCase.CreateStopwordCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateStopwordUseCase.ActivateStopwordCommand(
                null, null, null, null, id.toString(), null));

        Stopword loaded = get.execute(id.toString());
        assertEquals(StopwordStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
