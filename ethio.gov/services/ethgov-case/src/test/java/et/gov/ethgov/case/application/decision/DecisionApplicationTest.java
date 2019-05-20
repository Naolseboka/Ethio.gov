package et.gov.ethgov.case.application.decision;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.case.adapter.out.persistence.InMemoryDecisionRepository;
import et.gov.ethgov.case.domain.decision.Decision;
import et.gov.ethgov.case.domain.decision.DecisionStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DecisionApplicationTest {
    private InMemoryDecisionRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryDecisionRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateDecisionUseCase(repository, clock);
        var activate = new ActivateDecisionUseCase(repository, clock);
        var get = new GetDecisionUseCase(repository);

        var id = create.execute(new CreateDecisionUseCase.CreateDecisionCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateDecisionUseCase.ActivateDecisionCommand(
                null, null, null, null, id.toString(), null));

        Decision loaded = get.execute(id.toString());
        assertEquals(DecisionStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
