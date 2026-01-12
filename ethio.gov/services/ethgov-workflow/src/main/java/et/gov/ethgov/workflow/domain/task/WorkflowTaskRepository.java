package et.gov.ethgov.workflow.domain.task;

import et.gov.ethgov.workflow.domain.instance.ProcessInstanceId;
import java.util.List;
import java.util.Optional;

public interface WorkflowTaskRepository {

    Optional<WorkflowTask> findById(WorkflowTaskId id);

    Optional<WorkflowTask> findOpenOrClaimedByProcess(ProcessInstanceId processInstanceId);

    List<WorkflowTask> findByClaimedBy(String accountId);

    void save(WorkflowTask task);
}
