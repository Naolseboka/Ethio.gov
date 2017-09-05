package et.gov.ethgov.case.adapter.in.web;

import et.gov.ethgov.case.application.decision.ActivateDecisionUseCase;
import et.gov.ethgov.case.application.decision.CreateDecisionUseCase;
import et.gov.ethgov.case.application.decision.GetDecisionUseCase;
import et.gov.ethgov.case.application.decision.ListDecisionUseCase;
import et.gov.ethgov.case.application.decision.SuspendDecisionUseCase;
import et.gov.ethgov.case.application.decision.UpdateDecisionUseCase;
import et.gov.ethgov.case.domain.decision.Decision;
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
@RequestMapping("/v1/case/decision")
public class DecisionController {
    private final CreateDecisionUseCase createUseCase;
    private final UpdateDecisionUseCase updateUseCase;
    private final ActivateDecisionUseCase activateUseCase;
    private final SuspendDecisionUseCase suspendUseCase;
    private final GetDecisionUseCase getUseCase;
    private final ListDecisionUseCase listUseCase;

    public DecisionController(
            CreateDecisionUseCase createUseCase,
            UpdateDecisionUseCase updateUseCase,
            ActivateDecisionUseCase activateUseCase,
            SuspendDecisionUseCase suspendUseCase,
            GetDecisionUseCase getUseCase,
            ListDecisionUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DecisionIdResponse create(@Valid @RequestBody CreateDecisionRequest request) {
        var id = createUseCase.execute(new CreateDecisionUseCase.CreateDecisionCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new DecisionIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public DecisionResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<DecisionResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public DecisionIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateDecisionRequest request) {
        var updated = updateUseCase.execute(new UpdateDecisionUseCase.UpdateDecisionCommand(
                null, request.title(), request.description(), null, id, null));
        return new DecisionIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateDecisionUseCase.ActivateDecisionCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendDecisionRequest request) {
        suspendUseCase.execute(new SuspendDecisionUseCase.SuspendDecisionCommand(
                null, null, null, null, id, request.reason()));
    }

    private DecisionResponse toResponse(Decision aggregate) {
        return new DecisionResponse(
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

    public record CreateDecisionRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateDecisionRequest(String title, String description) {}

    public record SuspendDecisionRequest(@NotBlank String reason) {}

    public record DecisionIdResponse(String id) {}

    public record DecisionResponse(
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
