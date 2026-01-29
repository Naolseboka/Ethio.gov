package et.gov.ethgov.workflow.application.checklist;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.workflow.adapter.out.persistence.InMemoryChecklistRepository;
import et.gov.ethgov.workflow.domain.checklist.Checklist;
import et.gov.ethgov.workflow.domain.checklist.ChecklistStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ChecklistApplicationTest {
    private InMemoryChecklistRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryChecklistRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateChecklistUseCase(repository, clock);
        var activate = new ActivateChecklistUseCase(repository, clock);
        var get = new GetChecklistUseCase(repository);

        var id = create.execute(new CreateChecklistUseCase.CreateChecklistCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateChecklistUseCase.ActivateChecklistCommand(
                null, null, null, null, id.toString(), null));

        Checklist loaded = get.execute(id.toString());
        assertEquals(ChecklistStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
