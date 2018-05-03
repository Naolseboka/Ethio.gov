package et.gov.ethgov.payments.adapter.in.web;

import et.gov.ethgov.payments.application.invoice.ActivateInvoiceUseCase;
import et.gov.ethgov.payments.application.invoice.CreateInvoiceUseCase;
import et.gov.ethgov.payments.application.invoice.GetInvoiceUseCase;
import et.gov.ethgov.payments.application.invoice.ListInvoiceUseCase;
import et.gov.ethgov.payments.application.invoice.SuspendInvoiceUseCase;
import et.gov.ethgov.payments.application.invoice.UpdateInvoiceUseCase;
import et.gov.ethgov.payments.domain.invoice.Invoice;
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
@RequestMapping("/v1/payments/invoice")
public class InvoiceController {
    private final CreateInvoiceUseCase createUseCase;
    private final UpdateInvoiceUseCase updateUseCase;
    private final ActivateInvoiceUseCase activateUseCase;
    private final SuspendInvoiceUseCase suspendUseCase;
    private final GetInvoiceUseCase getUseCase;
    private final ListInvoiceUseCase listUseCase;

    public InvoiceController(
            CreateInvoiceUseCase createUseCase,
            UpdateInvoiceUseCase updateUseCase,
            ActivateInvoiceUseCase activateUseCase,
            SuspendInvoiceUseCase suspendUseCase,
            GetInvoiceUseCase getUseCase,
            ListInvoiceUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InvoiceIdResponse create(@Valid @RequestBody CreateInvoiceRequest request) {
        var id = createUseCase.execute(new CreateInvoiceUseCase.CreateInvoiceCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new InvoiceIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public InvoiceResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<InvoiceResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public InvoiceIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateInvoiceRequest request) {
        var updated = updateUseCase.execute(new UpdateInvoiceUseCase.UpdateInvoiceCommand(
                null, request.title(), request.description(), null, id, null));
        return new InvoiceIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateInvoiceUseCase.ActivateInvoiceCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendInvoiceRequest request) {
        suspendUseCase.execute(new SuspendInvoiceUseCase.SuspendInvoiceCommand(
                null, null, null, null, id, request.reason()));
    }

    private InvoiceResponse toResponse(Invoice aggregate) {
        return new InvoiceResponse(
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

    public record CreateInvoiceRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateInvoiceRequest(String title, String description) {}

    public record SuspendInvoiceRequest(@NotBlank String reason) {}

    public record InvoiceIdResponse(String id) {}

    public record InvoiceResponse(
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
