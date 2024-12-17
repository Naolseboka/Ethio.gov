package et.gov.ethgov.reporting.application.reportfilter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.reporting.adapter.out.persistence.InMemoryReportfilterRepository;
import et.gov.ethgov.reporting.domain.reportfilter.Reportfilter;
import et.gov.ethgov.reporting.domain.reportfilter.ReportfilterStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ReportfilterApplicationTest {
    private InMemoryReportfilterRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryReportfilterRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateReportfilterUseCase(repository, clock);
        var activate = new ActivateReportfilterUseCase(repository, clock);
        var get = new GetReportfilterUseCase(repository);

        var id = create.execute(new CreateReportfilterUseCase.CreateReportfilterCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateReportfilterUseCase.ActivateReportfilterCommand(
                null, null, null, null, id.toString(), null));

        Reportfilter loaded = get.execute(id.toString());
        assertEquals(ReportfilterStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
