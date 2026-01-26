package et.gov.ethgov.workflow.application.assignment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.workflow.adapter.out.persistence.InMemoryAssignmentRepository;
import et.gov.ethgov.workflow.domain.assignment.Assignment;
import et.gov.ethgov.workflow.domain.assignment.AssignmentStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AssignmentApplicationTest {
    private InMemoryAssignmentRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryAssignmentRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateAssignmentUseCase(repository, clock);
        var activate = new ActivateAssignmentUseCase(repository, clock);
        var get = new GetAssignmentUseCase(repository);

        var id = create.execute(new CreateAssignmentUseCase.CreateAssignmentCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateAssignmentUseCase.ActivateAssignmentCommand(
                null, null, null, null, id.toString(), null));

        Assignment loaded = get.execute(id.toString());
        assertEquals(AssignmentStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
