package et.gov.ethgov.licensing.application.compliance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.licensing.adapter.out.persistence.InMemoryComplianceRepository;
import et.gov.ethgov.licensing.domain.compliance.Compliance;
import et.gov.ethgov.licensing.domain.compliance.ComplianceStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ComplianceApplicationTest {
    private InMemoryComplianceRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryComplianceRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateComplianceUseCase(repository, clock);
        var activate = new ActivateComplianceUseCase(repository, clock);
        var get = new GetComplianceUseCase(repository);

        var id = create.execute(new CreateComplianceUseCase.CreateComplianceCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateComplianceUseCase.ActivateComplianceCommand(
                null, null, null, null, id.toString(), null));

        Compliance loaded = get.execute(id.toString());
        assertEquals(ComplianceStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
