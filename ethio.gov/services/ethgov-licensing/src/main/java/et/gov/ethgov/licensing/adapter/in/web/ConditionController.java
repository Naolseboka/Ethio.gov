package et.gov.ethgov.licensing.adapter.in.web;

import et.gov.ethgov.licensing.application.condition.ActivateConditionUseCase;
import et.gov.ethgov.licensing.application.condition.CreateConditionUseCase;
import et.gov.ethgov.licensing.application.condition.GetConditionUseCase;
import et.gov.ethgov.licensing.application.condition.ListConditionUseCase;
import et.gov.ethgov.licensing.application.condition.SuspendConditionUseCase;
import et.gov.ethgov.licensing.application.condition.UpdateConditionUseCase;
import et.gov.ethgov.licensing.domain.condition.Condition;
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
@RequestMapping("/v1/licensing/condition")
public class ConditionController {
    private final CreateConditionUseCase createUseCase;
    private final UpdateConditionUseCase updateUseCase;
    private final ActivateConditionUseCase activateUseCase;
    private final SuspendConditionUseCase suspendUseCase;
    private final GetConditionUseCase getUseCase;
    private final ListConditionUseCase listUseCase;

    public ConditionController(
            CreateConditionUseCase createUseCase,
            UpdateConditionUseCase updateUseCase,
            ActivateConditionUseCase activateUseCase,
            SuspendConditionUseCase suspendUseCase,
            GetConditionUseCase getUseCase,
            ListConditionUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ConditionIdResponse create(@Valid @RequestBody CreateConditionRequest request) {
        var id = createUseCase.execute(new CreateConditionUseCase.CreateConditionCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new ConditionIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public ConditionResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<ConditionResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public ConditionIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateConditionRequest request) {
        var updated = updateUseCase.execute(new UpdateConditionUseCase.UpdateConditionCommand(
                null, request.title(), request.description(), null, id, null));
        return new ConditionIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateConditionUseCase.ActivateConditionCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendConditionRequest request) {
        suspendUseCase.execute(new SuspendConditionUseCase.SuspendConditionCommand(
                null, null, null, null, id, request.reason()));
    }

    private ConditionResponse toResponse(Condition aggregate) {
        return new ConditionResponse(
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

    public record CreateConditionRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateConditionRequest(String title, String description) {}

    public record SuspendConditionRequest(@NotBlank String reason) {}

    public record ConditionIdResponse(String id) {}

    public record ConditionResponse(
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
