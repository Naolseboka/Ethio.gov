package et.gov.ethgov.workflow.adapter.in.web;

import et.gov.ethgov.workflow.application.assignment.ActivateAssignmentUseCase;
import et.gov.ethgov.workflow.application.assignment.CreateAssignmentUseCase;
import et.gov.ethgov.workflow.application.assignment.GetAssignmentUseCase;
import et.gov.ethgov.workflow.application.assignment.ListAssignmentUseCase;
import et.gov.ethgov.workflow.application.assignment.SuspendAssignmentUseCase;
import et.gov.ethgov.workflow.application.assignment.UpdateAssignmentUseCase;
import et.gov.ethgov.workflow.domain.assignment.Assignment;
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
@RequestMapping("/v1/workflow/assignment")
public class AssignmentController {
    private final CreateAssignmentUseCase createUseCase;
    private final UpdateAssignmentUseCase updateUseCase;
    private final ActivateAssignmentUseCase activateUseCase;
    private final SuspendAssignmentUseCase suspendUseCase;
    private final GetAssignmentUseCase getUseCase;
    private final ListAssignmentUseCase listUseCase;

    public AssignmentController(
            CreateAssignmentUseCase createUseCase,
            UpdateAssignmentUseCase updateUseCase,
            ActivateAssignmentUseCase activateUseCase,
            SuspendAssignmentUseCase suspendUseCase,
            GetAssignmentUseCase getUseCase,
            ListAssignmentUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AssignmentIdResponse create(@Valid @RequestBody CreateAssignmentRequest request) {
        var id = createUseCase.execute(new CreateAssignmentUseCase.CreateAssignmentCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new AssignmentIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public AssignmentResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<AssignmentResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public AssignmentIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateAssignmentRequest request) {
        var updated = updateUseCase.execute(new UpdateAssignmentUseCase.UpdateAssignmentCommand(
                null, request.title(), request.description(), null, id, null));
        return new AssignmentIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateAssignmentUseCase.ActivateAssignmentCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendAssignmentRequest request) {
        suspendUseCase.execute(new SuspendAssignmentUseCase.SuspendAssignmentCommand(
                null, null, null, null, id, request.reason()));
    }

    private AssignmentResponse toResponse(Assignment aggregate) {
        return new AssignmentResponse(
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

    public record CreateAssignmentRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateAssignmentRequest(String title, String description) {}

    public record SuspendAssignmentRequest(@NotBlank String reason) {}

    public record AssignmentIdResponse(String id) {}

    public record AssignmentResponse(
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
