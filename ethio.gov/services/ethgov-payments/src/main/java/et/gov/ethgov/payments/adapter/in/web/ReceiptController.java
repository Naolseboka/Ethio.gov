package et.gov.ethgov.payments.adapter.in.web;

import et.gov.ethgov.payments.application.receipt.ActivateReceiptUseCase;
import et.gov.ethgov.payments.application.receipt.CreateReceiptUseCase;
import et.gov.ethgov.payments.application.receipt.GetReceiptUseCase;
import et.gov.ethgov.payments.application.receipt.ListReceiptUseCase;
import et.gov.ethgov.payments.application.receipt.SuspendReceiptUseCase;
import et.gov.ethgov.payments.application.receipt.UpdateReceiptUseCase;
import et.gov.ethgov.payments.domain.receipt.Receipt;
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
@RequestMapping("/v1/payments/receipt")
public class ReceiptController {
    private final CreateReceiptUseCase createUseCase;
    private final UpdateReceiptUseCase updateUseCase;
    private final ActivateReceiptUseCase activateUseCase;
    private final SuspendReceiptUseCase suspendUseCase;
    private final GetReceiptUseCase getUseCase;
    private final ListReceiptUseCase listUseCase;

    public ReceiptController(
            CreateReceiptUseCase createUseCase,
            UpdateReceiptUseCase updateUseCase,
            ActivateReceiptUseCase activateUseCase,
            SuspendReceiptUseCase suspendUseCase,
            GetReceiptUseCase getUseCase,
            ListReceiptUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReceiptIdResponse create(@Valid @RequestBody CreateReceiptRequest request) {
        var id = createUseCase.execute(new CreateReceiptUseCase.CreateReceiptCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new ReceiptIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public ReceiptResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<ReceiptResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public ReceiptIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateReceiptRequest request) {
        var updated = updateUseCase.execute(new UpdateReceiptUseCase.UpdateReceiptCommand(
                null, request.title(), request.description(), null, id, null));
        return new ReceiptIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateReceiptUseCase.ActivateReceiptCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendReceiptRequest request) {
        suspendUseCase.execute(new SuspendReceiptUseCase.SuspendReceiptCommand(
                null, null, null, null, id, request.reason()));
    }

    private ReceiptResponse toResponse(Receipt aggregate) {
        return new ReceiptResponse(
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

    public record CreateReceiptRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateReceiptRequest(String title, String description) {}

    public record SuspendReceiptRequest(@NotBlank String reason) {}

    public record ReceiptIdResponse(String id) {}

    public record ReceiptResponse(
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
