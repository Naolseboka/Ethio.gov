package et.gov.ethgov.workflow.adapter.in.web;

import et.gov.ethgov.workflow.application.task.ClaimWorkflowTaskUseCase;
import et.gov.ethgov.workflow.application.task.CompleteWorkflowTaskUseCase;
import et.gov.ethgov.workflow.application.task.GetWorkflowTaskUseCase;
import et.gov.ethgov.workflow.domain.task.WorkflowTask;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/workflow-tasks")
public class WorkflowTaskController {

    private final ClaimWorkflowTaskUseCase claimTask;
    private final CompleteWorkflowTaskUseCase completeTask;
    private final GetWorkflowTaskUseCase getTask;

    public WorkflowTaskController(
            ClaimWorkflowTaskUseCase claimTask,
            CompleteWorkflowTaskUseCase completeTask,
            GetWorkflowTaskUseCase getTask) {
        this.claimTask = claimTask;
        this.completeTask = completeTask;
        this.getTask = getTask;
    }

    @GetMapping("/lookup")
    public List<TaskResponse> byClaimant(@RequestParam String claimedBy) {
        return getTask.byClaimedBy(claimedBy).stream().map(TaskResponse::from).toList();
    }

    @GetMapping("/{taskId}")
    public TaskResponse get(@PathVariable String taskId) {
        return TaskResponse.from(getTask.byId(taskId));
    }

    @PostMapping("/{taskId}/claim")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void claim(@PathVariable String taskId, @Valid @RequestBody ClaimRequest request) {
        claimTask.execute(taskId, request.accountId());
    }

    @PostMapping("/{taskId}/complete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void complete(@PathVariable String taskId, @Valid @RequestBody CompleteRequest request) {
        completeTask.execute(taskId, request.outcome(), request.accountId());
    }

    public record ClaimRequest(@NotBlank String accountId) {}

    public record CompleteRequest(@NotBlank String outcome, @NotBlank String accountId) {}

    public record TaskResponse(
            String taskId,
            String processInstanceId,
            String stepKey,
            String name,
            String status,
            String assigneeRole,
            String claimedByAccountId,
            String outcome) {

        static TaskResponse from(WorkflowTask task) {
            return new TaskResponse(
                    task.id().value(),
                    task.processInstanceId().value(),
                    task.stepKey(),
                    task.name(),
                    task.status().name(),
                    task.assigneeRole().orElse(null),
                    task.claimedByAccountId().orElse(null),
                    task.outcome().orElse(null));
        }
    }
}
