package et.gov.ethgov.workflow.adapter.in.web;

import et.gov.ethgov.workflow.application.task.ActivateTaskUseCase;
import et.gov.ethgov.workflow.application.task.CreateTaskUseCase;
import et.gov.ethgov.workflow.application.task.GetTaskUseCase;
import et.gov.ethgov.workflow.application.task.ListTaskUseCase;
import et.gov.ethgov.workflow.application.task.SuspendTaskUseCase;
import et.gov.ethgov.workflow.application.task.UpdateTaskUseCase;
import et.gov.ethgov.workflow.domain.task.Task;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/workflow/task")
public class TaskController {
    private final CreateTaskUseCase createUseCase;
    private final UpdateTaskUseCase updateUseCase;
    private final ActivateTaskUseCase activateUseCase;
    private final SuspendTaskUseCase suspendUseCase;
    private final GetTaskUseCase getUseCase;
    private final ListTaskUseCase listUseCase;

    public TaskController(
            CreateTaskUseCase createUseCase,
            UpdateTaskUseCase updateUseCase,
            ActivateTaskUseCase activateUseCase,
            SuspendTaskUseCase suspendUseCase,
            GetTaskUseCase getUseCase,
            ListTaskUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskIdResponse create(@Valid @RequestBody CreateTaskRequest request) {
        var id = createUseCase.execute(new CreateTaskUseCase.CreateTaskCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new TaskIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public TaskResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<TaskResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public TaskIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateTaskRequest request) {
        var updated = updateUseCase.execute(new UpdateTaskUseCase.UpdateTaskCommand(
                null, request.title(), request.description(), null, id, null));
        return new TaskIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateTaskUseCase.ActivateTaskCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendTaskRequest request) {
        suspendUseCase.execute(new SuspendTaskUseCase.SuspendTaskCommand(
                null, null, null, null, id, request.reason()));
    }

    private TaskResponse toResponse(Task aggregate) {
        return new TaskResponse(
                aggregate.id().toString(),
                aggregate.referenceCode(),
                aggregate.title(),
                aggregate.description(),
                aggregate.tenantId(),
                aggregate.status().name(),
                aggregate.createdAt().toString(),
                aggregate.updatedAt().toString(),
                aggregate.version());
    }

    public record CreateTaskRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateTaskRequest(String title, String description) {}

    public record SuspendTaskRequest(@NotBlank String reason) {}

    public record TaskIdResponse(String id) {}

    public record TaskResponse(
            String id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            String createdAt,
            String updatedAt,
            long version) {}
}
