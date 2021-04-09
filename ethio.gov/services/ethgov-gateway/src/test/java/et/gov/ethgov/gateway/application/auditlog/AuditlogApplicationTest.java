package et.gov.ethgov.gateway.application.auditlog;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.gateway.adapter.out.persistence.InMemoryAuditlogRepository;
import et.gov.ethgov.gateway.domain.auditlog.Auditlog;
import et.gov.ethgov.gateway.domain.auditlog.AuditlogStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AuditlogApplicationTest {
    private InMemoryAuditlogRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryAuditlogRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateAuditlogUseCase(repository, clock);
        var activate = new ActivateAuditlogUseCase(repository, clock);
        var get = new GetAuditlogUseCase(repository);

        var id = create.execute(new CreateAuditlogUseCase.CreateAuditlogCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateAuditlogUseCase.ActivateAuditlogCommand(
                null, null, null, null, id.toString(), null));

        Auditlog loaded = get.execute(id.toString());
        assertEquals(AuditlogStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
