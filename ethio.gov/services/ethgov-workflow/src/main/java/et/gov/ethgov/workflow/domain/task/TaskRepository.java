package et.gov.ethgov.workflow.domain.task;

import java.util.List;
import java.util.Optional;

public interface TaskRepository {
    Task save(Task aggregate);

    Optional<Task> findById(TaskId id);

    Optional<Task> findByReferenceCode(String tenantId, String referenceCode);

    List<Task> findByTenant(String tenantId, int limit);

    void delete(TaskId id);
}
