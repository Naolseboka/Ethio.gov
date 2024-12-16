package et.gov.ethgov.reporting.application.kpi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.reporting.adapter.out.persistence.InMemoryKpiRepository;
import et.gov.ethgov.reporting.domain.kpi.Kpi;
import et.gov.ethgov.reporting.domain.kpi.KpiStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class KpiApplicationTest {
    private InMemoryKpiRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryKpiRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateKpiUseCase(repository, clock);
        var activate = new ActivateKpiUseCase(repository, clock);
        var get = new GetKpiUseCase(repository);

        var id = create.execute(new CreateKpiUseCase.CreateKpiCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateKpiUseCase.ActivateKpiCommand(
                null, null, null, null, id.toString(), null));

        Kpi loaded = get.execute(id.toString());
        assertEquals(KpiStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
