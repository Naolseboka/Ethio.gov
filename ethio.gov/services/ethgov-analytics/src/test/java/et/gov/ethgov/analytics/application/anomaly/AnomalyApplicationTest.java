package et.gov.ethgov.analytics.application.anomaly;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.analytics.adapter.out.persistence.InMemoryAnomalyRepository;
import et.gov.ethgov.analytics.domain.anomaly.Anomaly;
import et.gov.ethgov.analytics.domain.anomaly.AnomalyStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AnomalyApplicationTest {
    private InMemoryAnomalyRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryAnomalyRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateAnomalyUseCase(repository, clock);
        var activate = new ActivateAnomalyUseCase(repository, clock);
        var get = new GetAnomalyUseCase(repository);

        var id = create.execute(new CreateAnomalyUseCase.CreateAnomalyCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateAnomalyUseCase.ActivateAnomalyCommand(
                null, null, null, null, id.toString(), null));

        Anomaly loaded = get.execute(id.toString());
        assertEquals(AnomalyStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
