package et.gov.ethgov.workflow.adapter.in.web;

import et.gov.ethgov.workflow.application.transition.ActivateTransitionUseCase;
import et.gov.ethgov.workflow.application.transition.CreateTransitionUseCase;
import et.gov.ethgov.workflow.application.transition.GetTransitionUseCase;
import et.gov.ethgov.workflow.application.transition.ListTransitionUseCase;
import et.gov.ethgov.workflow.application.transition.SuspendTransitionUseCase;
import et.gov.ethgov.workflow.application.transition.UpdateTransitionUseCase;
import et.gov.ethgov.workflow.domain.transition.Transition;
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
@RequestMapping("/v1/workflow/transition")
public class TransitionController {
    private final CreateTransitionUseCase createUseCase;
    private final UpdateTransitionUseCase updateUseCase;
    private final ActivateTransitionUseCase activateUseCase;
    private final SuspendTransitionUseCase suspendUseCase;
    private final GetTransitionUseCase getUseCase;
    private final ListTransitionUseCase listUseCase;

    public TransitionController(
            CreateTransitionUseCase createUseCase,
            UpdateTransitionUseCase updateUseCase,
            ActivateTransitionUseCase activateUseCase,
            SuspendTransitionUseCase suspendUseCase,
            GetTransitionUseCase getUseCase,
            ListTransitionUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransitionIdResponse create(@Valid @RequestBody CreateTransitionRequest request) {
        var id = createUseCase.execute(new CreateTransitionUseCase.CreateTransitionCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new TransitionIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public TransitionResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<TransitionResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public TransitionIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateTransitionRequest request) {
        var updated = updateUseCase.execute(new UpdateTransitionUseCase.UpdateTransitionCommand(
                null, request.title(), request.description(), null, id, null));
        return new TransitionIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateTransitionUseCase.ActivateTransitionCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendTransitionRequest request) {
        suspendUseCase.execute(new SuspendTransitionUseCase.SuspendTransitionCommand(
                null, null, null, null, id, request.reason()));
    }

    private TransitionResponse toResponse(Transition aggregate) {
        return new TransitionResponse(
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

    public record CreateTransitionRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateTransitionRequest(String title, String description) {}

    public record SuspendTransitionRequest(@NotBlank String reason) {}

    public record TransitionIdResponse(String id) {}

    public record TransitionResponse(
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
