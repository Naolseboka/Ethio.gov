package et.gov.ethgov.licensing.application.condition;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.licensing.adapter.out.persistence.InMemoryConditionRepository;
import et.gov.ethgov.licensing.domain.condition.Condition;
import et.gov.ethgov.licensing.domain.condition.ConditionStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ConditionApplicationTest {
    private InMemoryConditionRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryConditionRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateConditionUseCase(repository, clock);
        var activate = new ActivateConditionUseCase(repository, clock);
        var get = new GetConditionUseCase(repository);

        var id = create.execute(new CreateConditionUseCase.CreateConditionCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateConditionUseCase.ActivateConditionCommand(
                null, null, null, null, id.toString(), null));

        Condition loaded = get.execute(id.toString());
        assertEquals(ConditionStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
