package et.gov.ethgov.identity.adapter.in.web;

import et.gov.ethgov.identity.application.recovery.ActivateRecoveryUseCase;
import et.gov.ethgov.identity.application.recovery.CreateRecoveryUseCase;
import et.gov.ethgov.identity.application.recovery.GetRecoveryUseCase;
import et.gov.ethgov.identity.application.recovery.ListRecoveryUseCase;
import et.gov.ethgov.identity.application.recovery.SuspendRecoveryUseCase;
import et.gov.ethgov.identity.application.recovery.UpdateRecoveryUseCase;
import et.gov.ethgov.identity.domain.recovery.Recovery;
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
@RequestMapping("/v1/identity/recovery")
public class RecoveryController {
    private final CreateRecoveryUseCase createUseCase;
    private final UpdateRecoveryUseCase updateUseCase;
    private final ActivateRecoveryUseCase activateUseCase;
    private final SuspendRecoveryUseCase suspendUseCase;
    private final GetRecoveryUseCase getUseCase;
    private final ListRecoveryUseCase listUseCase;

    public RecoveryController(
            CreateRecoveryUseCase createUseCase,
            UpdateRecoveryUseCase updateUseCase,
            ActivateRecoveryUseCase activateUseCase,
            SuspendRecoveryUseCase suspendUseCase,
            GetRecoveryUseCase getUseCase,
            ListRecoveryUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RecoveryIdResponse create(@Valid @RequestBody CreateRecoveryRequest request) {
        var id = createUseCase.execute(new CreateRecoveryUseCase.CreateRecoveryCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new RecoveryIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public RecoveryResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<RecoveryResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public RecoveryIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateRecoveryRequest request) {
        var updated = updateUseCase.execute(new UpdateRecoveryUseCase.UpdateRecoveryCommand(
                null, request.title(), request.description(), null, id, null));
        return new RecoveryIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateRecoveryUseCase.ActivateRecoveryCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendRecoveryRequest request) {
        suspendUseCase.execute(new SuspendRecoveryUseCase.SuspendRecoveryCommand(
                null, null, null, null, id, request.reason()));
    }

    private RecoveryResponse toResponse(Recovery aggregate) {
        return new RecoveryResponse(
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

    public record CreateRecoveryRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateRecoveryRequest(String title, String description) {}

    public record SuspendRecoveryRequest(@NotBlank String reason) {}

    public record RecoveryIdResponse(String id) {}

    public record RecoveryResponse(
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
