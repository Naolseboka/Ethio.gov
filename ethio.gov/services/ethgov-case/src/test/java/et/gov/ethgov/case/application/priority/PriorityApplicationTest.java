package et.gov.ethgov.case.application.priority;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.case.adapter.out.persistence.InMemoryPriorityRepository;
import et.gov.ethgov.case.domain.priority.Priority;
import et.gov.ethgov.case.domain.priority.PriorityStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PriorityApplicationTest {
    private InMemoryPriorityRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryPriorityRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreatePriorityUseCase(repository, clock);
        var activate = new ActivatePriorityUseCase(repository, clock);
        var get = new GetPriorityUseCase(repository);

        var id = create.execute(new CreatePriorityUseCase.CreatePriorityCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivatePriorityUseCase.ActivatePriorityCommand(
                null, null, null, null, id.toString(), null));

        Priority loaded = get.execute(id.toString());
        assertEquals(PriorityStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
