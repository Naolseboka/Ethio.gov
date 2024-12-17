package et.gov.ethgov.reporting.application.reportalert;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.reporting.adapter.out.persistence.InMemoryReportalertRepository;
import et.gov.ethgov.reporting.domain.reportalert.Reportalert;
import et.gov.ethgov.reporting.domain.reportalert.ReportalertStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ReportalertApplicationTest {
    private InMemoryReportalertRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryReportalertRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateReportalertUseCase(repository, clock);
        var activate = new ActivateReportalertUseCase(repository, clock);
        var get = new GetReportalertUseCase(repository);

        var id = create.execute(new CreateReportalertUseCase.CreateReportalertCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateReportalertUseCase.ActivateReportalertCommand(
                null, null, null, null, id.toString(), null));

        Reportalert loaded = get.execute(id.toString());
        assertEquals(ReportalertStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
