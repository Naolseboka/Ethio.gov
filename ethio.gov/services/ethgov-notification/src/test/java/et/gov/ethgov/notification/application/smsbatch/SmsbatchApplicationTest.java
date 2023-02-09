package et.gov.ethgov.notification.application.smsbatch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.notification.adapter.out.persistence.InMemorySmsbatchRepository;
import et.gov.ethgov.notification.domain.smsbatch.Smsbatch;
import et.gov.ethgov.notification.domain.smsbatch.SmsbatchStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SmsbatchApplicationTest {
    private InMemorySmsbatchRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemorySmsbatchRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateSmsbatchUseCase(repository, clock);
        var activate = new ActivateSmsbatchUseCase(repository, clock);
        var get = new GetSmsbatchUseCase(repository);

        var id = create.execute(new CreateSmsbatchUseCase.CreateSmsbatchCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateSmsbatchUseCase.ActivateSmsbatchCommand(
                null, null, null, null, id.toString(), null));

        Smsbatch loaded = get.execute(id.toString());
        assertEquals(SmsbatchStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
