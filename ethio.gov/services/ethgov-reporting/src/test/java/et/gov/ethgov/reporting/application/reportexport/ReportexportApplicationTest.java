package et.gov.ethgov.reporting.application.reportexport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.reporting.adapter.out.persistence.InMemoryReportexportRepository;
import et.gov.ethgov.reporting.domain.reportexport.Reportexport;
import et.gov.ethgov.reporting.domain.reportexport.ReportexportStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ReportexportApplicationTest {
    private InMemoryReportexportRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryReportexportRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateReportexportUseCase(repository, clock);
        var activate = new ActivateReportexportUseCase(repository, clock);
        var get = new GetReportexportUseCase(repository);

        var id = create.execute(new CreateReportexportUseCase.CreateReportexportCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateReportexportUseCase.ActivateReportexportCommand(
                null, null, null, null, id.toString(), null));

        Reportexport loaded = get.execute(id.toString());
        assertEquals(ReportexportStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
