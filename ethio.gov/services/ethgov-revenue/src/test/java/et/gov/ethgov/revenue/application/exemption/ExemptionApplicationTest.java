package et.gov.ethgov.revenue.application.exemption;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.revenue.adapter.out.persistence.InMemoryExemptionRepository;
import et.gov.ethgov.revenue.domain.exemption.Exemption;
import et.gov.ethgov.revenue.domain.exemption.ExemptionStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ExemptionApplicationTest {
    private InMemoryExemptionRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryExemptionRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateExemptionUseCase(repository, clock);
        var activate = new ActivateExemptionUseCase(repository, clock);
        var get = new GetExemptionUseCase(repository);

        var id = create.execute(new CreateExemptionUseCase.CreateExemptionCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateExemptionUseCase.ActivateExemptionCommand(
                null, null, null, null, id.toString(), null));

        Exemption loaded = get.execute(id.toString());
        assertEquals(ExemptionStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
