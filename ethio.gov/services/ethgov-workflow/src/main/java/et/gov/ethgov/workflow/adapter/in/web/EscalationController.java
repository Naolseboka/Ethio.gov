package et.gov.ethgov.workflow.adapter.in.web;

import et.gov.ethgov.workflow.application.escalation.ActivateEscalationUseCase;
import et.gov.ethgov.workflow.application.escalation.CreateEscalationUseCase;
import et.gov.ethgov.workflow.application.escalation.GetEscalationUseCase;
import et.gov.ethgov.workflow.application.escalation.ListEscalationUseCase;
import et.gov.ethgov.workflow.application.escalation.SuspendEscalationUseCase;
import et.gov.ethgov.workflow.application.escalation.UpdateEscalationUseCase;
import et.gov.ethgov.workflow.domain.escalation.Escalation;
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
@RequestMapping("/v1/workflow/escalation")
public class EscalationController {
    private final CreateEscalationUseCase createUseCase;
    private final UpdateEscalationUseCase updateUseCase;
    private final ActivateEscalationUseCase activateUseCase;
    private final SuspendEscalationUseCase suspendUseCase;
    private final GetEscalationUseCase getUseCase;
    private final ListEscalationUseCase listUseCase;

    public EscalationController(
            CreateEscalationUseCase createUseCase,
            UpdateEscalationUseCase updateUseCase,
            ActivateEscalationUseCase activateUseCase,
            SuspendEscalationUseCase suspendUseCase,
            GetEscalationUseCase getUseCase,
            ListEscalationUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EscalationIdResponse create(@Valid @RequestBody CreateEscalationRequest request) {
        var id = createUseCase.execute(new CreateEscalationUseCase.CreateEscalationCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new EscalationIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public EscalationResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<EscalationResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public EscalationIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateEscalationRequest request) {
        var updated = updateUseCase.execute(new UpdateEscalationUseCase.UpdateEscalationCommand(
                null, request.title(), request.description(), null, id, null));
        return new EscalationIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateEscalationUseCase.ActivateEscalationCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendEscalationRequest request) {
        suspendUseCase.execute(new SuspendEscalationUseCase.SuspendEscalationCommand(
                null, null, null, null, id, request.reason()));
    }

    private EscalationResponse toResponse(Escalation aggregate) {
        return new EscalationResponse(
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

    public record CreateEscalationRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateEscalationRequest(String title, String description) {}

    public record SuspendEscalationRequest(@NotBlank String reason) {}

    public record EscalationIdResponse(String id) {}

    public record EscalationResponse(
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
