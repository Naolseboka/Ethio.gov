package et.gov.ethgov.workflow.application.sla;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.workflow.adapter.out.persistence.InMemorySlaRepository;
import et.gov.ethgov.workflow.domain.sla.Sla;
import et.gov.ethgov.workflow.domain.sla.SlaStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SlaApplicationTest {
    private InMemorySlaRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemorySlaRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateSlaUseCase(repository, clock);
        var activate = new ActivateSlaUseCase(repository, clock);
        var get = new GetSlaUseCase(repository);

        var id = create.execute(new CreateSlaUseCase.CreateSlaCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateSlaUseCase.ActivateSlaCommand(
                null, null, null, null, id.toString(), null));

        Sla loaded = get.execute(id.toString());
        assertEquals(SlaStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
