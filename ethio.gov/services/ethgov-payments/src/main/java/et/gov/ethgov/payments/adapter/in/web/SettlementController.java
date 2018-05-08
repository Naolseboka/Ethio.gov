package et.gov.ethgov.payments.adapter.in.web;

import et.gov.ethgov.payments.application.settlement.ActivateSettlementUseCase;
import et.gov.ethgov.payments.application.settlement.CreateSettlementUseCase;
import et.gov.ethgov.payments.application.settlement.GetSettlementUseCase;
import et.gov.ethgov.payments.application.settlement.ListSettlementUseCase;
import et.gov.ethgov.payments.application.settlement.SuspendSettlementUseCase;
import et.gov.ethgov.payments.application.settlement.UpdateSettlementUseCase;
import et.gov.ethgov.payments.domain.settlement.Settlement;
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
@RequestMapping("/v1/payments/settlement")
public class SettlementController {
    private final CreateSettlementUseCase createUseCase;
    private final UpdateSettlementUseCase updateUseCase;
    private final ActivateSettlementUseCase activateUseCase;
    private final SuspendSettlementUseCase suspendUseCase;
    private final GetSettlementUseCase getUseCase;
    private final ListSettlementUseCase listUseCase;

    public SettlementController(
            CreateSettlementUseCase createUseCase,
            UpdateSettlementUseCase updateUseCase,
            ActivateSettlementUseCase activateUseCase,
            SuspendSettlementUseCase suspendUseCase,
            GetSettlementUseCase getUseCase,
            ListSettlementUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SettlementIdResponse create(@Valid @RequestBody CreateSettlementRequest request) {
        var id = createUseCase.execute(new CreateSettlementUseCase.CreateSettlementCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new SettlementIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public SettlementResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<SettlementResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public SettlementIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateSettlementRequest request) {
        var updated = updateUseCase.execute(new UpdateSettlementUseCase.UpdateSettlementCommand(
                null, request.title(), request.description(), null, id, null));
        return new SettlementIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateSettlementUseCase.ActivateSettlementCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendSettlementRequest request) {
        suspendUseCase.execute(new SuspendSettlementUseCase.SuspendSettlementCommand(
                null, null, null, null, id, request.reason()));
    }

    private SettlementResponse toResponse(Settlement aggregate) {
        return new SettlementResponse(
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

    public record CreateSettlementRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateSettlementRequest(String title, String description) {}

    public record SuspendSettlementRequest(@NotBlank String reason) {}

    public record SettlementIdResponse(String id) {}

    public record SettlementResponse(
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
