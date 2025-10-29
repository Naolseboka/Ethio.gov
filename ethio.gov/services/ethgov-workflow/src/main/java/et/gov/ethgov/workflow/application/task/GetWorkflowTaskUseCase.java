package et.gov.ethgov.workflow.application.task;

import et.gov.ethgov.platform.commons.error.DomainException;
import et.gov.ethgov.workflow.domain.task.WorkflowTask;
import et.gov.ethgov.workflow.domain.task.WorkflowTaskId;
import et.gov.ethgov.workflow.domain.task.WorkflowTaskRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetWorkflowTaskUseCase {

    private final WorkflowTaskRepository tasks;

    public GetWorkflowTaskUseCase(WorkflowTaskRepository tasks) {
        this.tasks = tasks;
    }

    @Transactional(readOnly = true)
    public WorkflowTask byId(String taskId) {
        return tasks.findById(WorkflowTaskId.of(taskId))
                .orElseThrow(() -> new DomainException(
                        "TASK_NOT_FOUND",
                        "Task Not Found",
                        "Workflow task does not exist",
                        404));
    }

    @Transactional(readOnly = true)
    public List<WorkflowTask> byClaimedBy(String accountId) {
        return tasks.findByClaimedBy(accountId);
    }
}
