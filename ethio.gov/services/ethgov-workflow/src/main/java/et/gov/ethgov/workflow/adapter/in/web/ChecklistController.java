package et.gov.ethgov.workflow.adapter.in.web;

import et.gov.ethgov.workflow.application.checklist.ActivateChecklistUseCase;
import et.gov.ethgov.workflow.application.checklist.CreateChecklistUseCase;
import et.gov.ethgov.workflow.application.checklist.GetChecklistUseCase;
import et.gov.ethgov.workflow.application.checklist.ListChecklistUseCase;
import et.gov.ethgov.workflow.application.checklist.SuspendChecklistUseCase;
import et.gov.ethgov.workflow.application.checklist.UpdateChecklistUseCase;
import et.gov.ethgov.workflow.domain.checklist.Checklist;
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
@RequestMapping("/v1/workflow/checklist")
public class ChecklistController {
    private final CreateChecklistUseCase createUseCase;
    private final UpdateChecklistUseCase updateUseCase;
    private final ActivateChecklistUseCase activateUseCase;
    private final SuspendChecklistUseCase suspendUseCase;
    private final GetChecklistUseCase getUseCase;
    private final ListChecklistUseCase listUseCase;

    public ChecklistController(
            CreateChecklistUseCase createUseCase,
            UpdateChecklistUseCase updateUseCase,
            ActivateChecklistUseCase activateUseCase,
            SuspendChecklistUseCase suspendUseCase,
            GetChecklistUseCase getUseCase,
            ListChecklistUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ChecklistIdResponse create(@Valid @RequestBody CreateChecklistRequest request) {
        var id = createUseCase.execute(new CreateChecklistUseCase.CreateChecklistCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new ChecklistIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public ChecklistResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<ChecklistResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public ChecklistIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateChecklistRequest request) {
        var updated = updateUseCase.execute(new UpdateChecklistUseCase.UpdateChecklistCommand(
                null, request.title(), request.description(), null, id, null));
        return new ChecklistIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateChecklistUseCase.ActivateChecklistCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendChecklistRequest request) {
        suspendUseCase.execute(new SuspendChecklistUseCase.SuspendChecklistCommand(
                null, null, null, null, id, request.reason()));
    }

    private ChecklistResponse toResponse(Checklist aggregate) {
        return new ChecklistResponse(
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

    public record CreateChecklistRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateChecklistRequest(String title, String description) {}

    public record SuspendChecklistRequest(@NotBlank String reason) {}

    public record ChecklistIdResponse(String id) {}

    public record ChecklistResponse(
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
