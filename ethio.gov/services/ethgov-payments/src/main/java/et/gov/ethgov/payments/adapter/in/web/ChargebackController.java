package et.gov.ethgov.payments.adapter.in.web;

import et.gov.ethgov.payments.application.chargeback.ActivateChargebackUseCase;
import et.gov.ethgov.payments.application.chargeback.CreateChargebackUseCase;
import et.gov.ethgov.payments.application.chargeback.GetChargebackUseCase;
import et.gov.ethgov.payments.application.chargeback.ListChargebackUseCase;
import et.gov.ethgov.payments.application.chargeback.SuspendChargebackUseCase;
import et.gov.ethgov.payments.application.chargeback.UpdateChargebackUseCase;
import et.gov.ethgov.payments.domain.chargeback.Chargeback;
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
@RequestMapping("/v1/payments/chargeback")
public class ChargebackController {
    private final CreateChargebackUseCase createUseCase;
    private final UpdateChargebackUseCase updateUseCase;
    private final ActivateChargebackUseCase activateUseCase;
    private final SuspendChargebackUseCase suspendUseCase;
    private final GetChargebackUseCase getUseCase;
    private final ListChargebackUseCase listUseCase;

    public ChargebackController(
            CreateChargebackUseCase createUseCase,
            UpdateChargebackUseCase updateUseCase,
            ActivateChargebackUseCase activateUseCase,
            SuspendChargebackUseCase suspendUseCase,
            GetChargebackUseCase getUseCase,
            ListChargebackUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ChargebackIdResponse create(@Valid @RequestBody CreateChargebackRequest request) {
        var id = createUseCase.execute(new CreateChargebackUseCase.CreateChargebackCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new ChargebackIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public ChargebackResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<ChargebackResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public ChargebackIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateChargebackRequest request) {
        var updated = updateUseCase.execute(new UpdateChargebackUseCase.UpdateChargebackCommand(
                null, request.title(), request.description(), null, id, null));
        return new ChargebackIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateChargebackUseCase.ActivateChargebackCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendChargebackRequest request) {
        suspendUseCase.execute(new SuspendChargebackUseCase.SuspendChargebackCommand(
                null, null, null, null, id, request.reason()));
    }

    private ChargebackResponse toResponse(Chargeback aggregate) {
        return new ChargebackResponse(
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

    public record CreateChargebackRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateChargebackRequest(String title, String description) {}

    public record SuspendChargebackRequest(@NotBlank String reason) {}

    public record ChargebackIdResponse(String id) {}

    public record ChargebackResponse(
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
