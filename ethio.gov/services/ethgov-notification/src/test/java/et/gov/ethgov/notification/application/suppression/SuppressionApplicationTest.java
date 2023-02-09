package et.gov.ethgov.notification.application.suppression;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.notification.adapter.out.persistence.InMemorySuppressionRepository;
import et.gov.ethgov.notification.domain.suppression.Suppression;
import et.gov.ethgov.notification.domain.suppression.SuppressionStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SuppressionApplicationTest {
    private InMemorySuppressionRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemorySuppressionRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateSuppressionUseCase(repository, clock);
        var activate = new ActivateSuppressionUseCase(repository, clock);
        var get = new GetSuppressionUseCase(repository);

        var id = create.execute(new CreateSuppressionUseCase.CreateSuppressionCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateSuppressionUseCase.ActivateSuppressionCommand(
                null, null, null, null, id.toString(), null));

        Suppression loaded = get.execute(id.toString());
        assertEquals(SuppressionStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
