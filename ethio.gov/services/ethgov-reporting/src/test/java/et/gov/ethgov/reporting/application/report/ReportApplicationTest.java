package et.gov.ethgov.reporting.application.report;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.reporting.adapter.out.persistence.InMemoryReportRepository;
import et.gov.ethgov.reporting.domain.report.Report;
import et.gov.ethgov.reporting.domain.report.ReportStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ReportApplicationTest {
    private InMemoryReportRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryReportRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateReportUseCase(repository, clock);
        var activate = new ActivateReportUseCase(repository, clock);
        var get = new GetReportUseCase(repository);

        var id = create.execute(new CreateReportUseCase.CreateReportCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateReportUseCase.ActivateReportCommand(
                null, null, null, null, id.toString(), null));

        Report loaded = get.execute(id.toString());
        assertEquals(ReportStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
