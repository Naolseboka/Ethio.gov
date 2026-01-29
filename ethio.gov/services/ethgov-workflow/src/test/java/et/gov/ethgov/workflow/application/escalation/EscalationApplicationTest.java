package et.gov.ethgov.workflow.application.escalation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.workflow.adapter.out.persistence.InMemoryEscalationRepository;
import et.gov.ethgov.workflow.domain.escalation.Escalation;
import et.gov.ethgov.workflow.domain.escalation.EscalationStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EscalationApplicationTest {
    private InMemoryEscalationRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryEscalationRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateEscalationUseCase(repository, clock);
        var activate = new ActivateEscalationUseCase(repository, clock);
        var get = new GetEscalationUseCase(repository);

        var id = create.execute(new CreateEscalationUseCase.CreateEscalationCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateEscalationUseCase.ActivateEscalationCommand(
                null, null, null, null, id.toString(), null));

        Escalation loaded = get.execute(id.toString());
        assertEquals(EscalationStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
