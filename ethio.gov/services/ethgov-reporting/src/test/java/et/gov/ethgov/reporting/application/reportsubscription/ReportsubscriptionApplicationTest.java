package et.gov.ethgov.reporting.application.reportsubscription;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.reporting.adapter.out.persistence.InMemoryReportsubscriptionRepository;
import et.gov.ethgov.reporting.domain.reportsubscription.Reportsubscription;
import et.gov.ethgov.reporting.domain.reportsubscription.ReportsubscriptionStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ReportsubscriptionApplicationTest {
    private InMemoryReportsubscriptionRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryReportsubscriptionRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateReportsubscriptionUseCase(repository, clock);
        var activate = new ActivateReportsubscriptionUseCase(repository, clock);
        var get = new GetReportsubscriptionUseCase(repository);

        var id = create.execute(new CreateReportsubscriptionUseCase.CreateReportsubscriptionCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateReportsubscriptionUseCase.ActivateReportsubscriptionCommand(
                null, null, null, null, id.toString(), null));

        Reportsubscription loaded = get.execute(id.toString());
        assertEquals(ReportsubscriptionStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
