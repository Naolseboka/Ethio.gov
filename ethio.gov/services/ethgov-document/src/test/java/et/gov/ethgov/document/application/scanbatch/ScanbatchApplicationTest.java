package et.gov.ethgov.document.application.scanbatch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.document.adapter.out.persistence.InMemoryScanbatchRepository;
import et.gov.ethgov.document.domain.scanbatch.Scanbatch;
import et.gov.ethgov.document.domain.scanbatch.ScanbatchStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ScanbatchApplicationTest {
    private InMemoryScanbatchRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryScanbatchRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateScanbatchUseCase(repository, clock);
        var activate = new ActivateScanbatchUseCase(repository, clock);
        var get = new GetScanbatchUseCase(repository);

        var id = create.execute(new CreateScanbatchUseCase.CreateScanbatchCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateScanbatchUseCase.ActivateScanbatchCommand(
                null, null, null, null, id.toString(), null));

        Scanbatch loaded = get.execute(id.toString());
        assertEquals(ScanbatchStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
