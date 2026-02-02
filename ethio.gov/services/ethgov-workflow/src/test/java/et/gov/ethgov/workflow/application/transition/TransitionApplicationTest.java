package et.gov.ethgov.workflow.application.transition;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.workflow.adapter.out.persistence.InMemoryTransitionRepository;
import et.gov.ethgov.workflow.domain.transition.Transition;
import et.gov.ethgov.workflow.domain.transition.TransitionStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TransitionApplicationTest {
    private InMemoryTransitionRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryTransitionRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateTransitionUseCase(repository, clock);
        var activate = new ActivateTransitionUseCase(repository, clock);
        var get = new GetTransitionUseCase(repository);

        var id = create.execute(new CreateTransitionUseCase.CreateTransitionCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateTransitionUseCase.ActivateTransitionCommand(
                null, null, null, null, id.toString(), null));

        Transition loaded = get.execute(id.toString());
        assertEquals(TransitionStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
