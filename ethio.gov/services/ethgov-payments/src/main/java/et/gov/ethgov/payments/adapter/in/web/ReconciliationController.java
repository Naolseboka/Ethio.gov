package et.gov.ethgov.payments.adapter.in.web;

import et.gov.ethgov.payments.application.reconciliation.ActivateReconciliationUseCase;
import et.gov.ethgov.payments.application.reconciliation.CreateReconciliationUseCase;
import et.gov.ethgov.payments.application.reconciliation.GetReconciliationUseCase;
import et.gov.ethgov.payments.application.reconciliation.ListReconciliationUseCase;
import et.gov.ethgov.payments.application.reconciliation.SuspendReconciliationUseCase;
import et.gov.ethgov.payments.application.reconciliation.UpdateReconciliationUseCase;
import et.gov.ethgov.payments.domain.reconciliation.Reconciliation;
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
@RequestMapping("/v1/payments/reconciliation")
public class ReconciliationController {
    private final CreateReconciliationUseCase createUseCase;
    private final UpdateReconciliationUseCase updateUseCase;
    private final ActivateReconciliationUseCase activateUseCase;
    private final SuspendReconciliationUseCase suspendUseCase;
    private final GetReconciliationUseCase getUseCase;
    private final ListReconciliationUseCase listUseCase;

    public ReconciliationController(
            CreateReconciliationUseCase createUseCase,
            UpdateReconciliationUseCase updateUseCase,
            ActivateReconciliationUseCase activateUseCase,
            SuspendReconciliationUseCase suspendUseCase,
            GetReconciliationUseCase getUseCase,
            ListReconciliationUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReconciliationIdResponse create(@Valid @RequestBody CreateReconciliationRequest request) {
        var id = createUseCase.execute(new CreateReconciliationUseCase.CreateReconciliationCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new ReconciliationIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public ReconciliationResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<ReconciliationResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public ReconciliationIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateReconciliationRequest request) {
        var updated = updateUseCase.execute(new UpdateReconciliationUseCase.UpdateReconciliationCommand(
                null, request.title(), request.description(), null, id, null));
        return new ReconciliationIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateReconciliationUseCase.ActivateReconciliationCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendReconciliationRequest request) {
        suspendUseCase.execute(new SuspendReconciliationUseCase.SuspendReconciliationCommand(
                null, null, null, null, id, request.reason()));
    }

    private ReconciliationResponse toResponse(Reconciliation aggregate) {
        return new ReconciliationResponse(
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

    public record CreateReconciliationRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateReconciliationRequest(String title, String description) {}

    public record SuspendReconciliationRequest(@NotBlank String reason) {}

    public record ReconciliationIdResponse(String id) {}

    public record ReconciliationResponse(
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
