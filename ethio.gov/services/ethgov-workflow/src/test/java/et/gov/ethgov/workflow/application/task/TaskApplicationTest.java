package et.gov.ethgov.workflow.application.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.workflow.adapter.out.persistence.InMemoryTaskRepository;
import et.gov.ethgov.workflow.domain.task.Task;
import et.gov.ethgov.workflow.domain.task.TaskStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TaskApplicationTest {
    private InMemoryTaskRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryTaskRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateTaskUseCase(repository, clock);
        var activate = new ActivateTaskUseCase(repository, clock);
        var get = new GetTaskUseCase(repository);

        var id = create.execute(new CreateTaskUseCase.CreateTaskCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateTaskUseCase.ActivateTaskCommand(
                null, null, null, null, id.toString(), null));

        Task loaded = get.execute(id.toString());
        assertEquals(TaskStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
