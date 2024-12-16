package et.gov.ethgov.reporting.application.metric;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.reporting.adapter.out.persistence.InMemoryMetricRepository;
import et.gov.ethgov.reporting.domain.metric.Metric;
import et.gov.ethgov.reporting.domain.metric.MetricStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MetricApplicationTest {
    private InMemoryMetricRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryMetricRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateMetricUseCase(repository, clock);
        var activate = new ActivateMetricUseCase(repository, clock);
        var get = new GetMetricUseCase(repository);

        var id = create.execute(new CreateMetricUseCase.CreateMetricCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateMetricUseCase.ActivateMetricCommand(
                null, null, null, null, id.toString(), null));

        Metric loaded = get.execute(id.toString());
        assertEquals(MetricStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
