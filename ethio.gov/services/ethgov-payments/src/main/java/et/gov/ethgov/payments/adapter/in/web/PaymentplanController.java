package et.gov.ethgov.payments.adapter.in.web;

import et.gov.ethgov.payments.application.paymentplan.ActivatePaymentplanUseCase;
import et.gov.ethgov.payments.application.paymentplan.CreatePaymentplanUseCase;
import et.gov.ethgov.payments.application.paymentplan.GetPaymentplanUseCase;
import et.gov.ethgov.payments.application.paymentplan.ListPaymentplanUseCase;
import et.gov.ethgov.payments.application.paymentplan.SuspendPaymentplanUseCase;
import et.gov.ethgov.payments.application.paymentplan.UpdatePaymentplanUseCase;
import et.gov.ethgov.payments.domain.paymentplan.Paymentplan;
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
@RequestMapping("/v1/payments/paymentplan")
public class PaymentplanController {
    private final CreatePaymentplanUseCase createUseCase;
    private final UpdatePaymentplanUseCase updateUseCase;
    private final ActivatePaymentplanUseCase activateUseCase;
    private final SuspendPaymentplanUseCase suspendUseCase;
    private final GetPaymentplanUseCase getUseCase;
    private final ListPaymentplanUseCase listUseCase;

    public PaymentplanController(
            CreatePaymentplanUseCase createUseCase,
            UpdatePaymentplanUseCase updateUseCase,
            ActivatePaymentplanUseCase activateUseCase,
            SuspendPaymentplanUseCase suspendUseCase,
            GetPaymentplanUseCase getUseCase,
            ListPaymentplanUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentplanIdResponse create(@Valid @RequestBody CreatePaymentplanRequest request) {
        var id = createUseCase.execute(new CreatePaymentplanUseCase.CreatePaymentplanCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new PaymentplanIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public PaymentplanResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<PaymentplanResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public PaymentplanIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdatePaymentplanRequest request) {
        var updated = updateUseCase.execute(new UpdatePaymentplanUseCase.UpdatePaymentplanCommand(
                null, request.title(), request.description(), null, id, null));
        return new PaymentplanIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivatePaymentplanUseCase.ActivatePaymentplanCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendPaymentplanRequest request) {
        suspendUseCase.execute(new SuspendPaymentplanUseCase.SuspendPaymentplanCommand(
                null, null, null, null, id, request.reason()));
    }

    private PaymentplanResponse toResponse(Paymentplan aggregate) {
        return new PaymentplanResponse(
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

    public record CreatePaymentplanRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdatePaymentplanRequest(String title, String description) {}

    public record SuspendPaymentplanRequest(@NotBlank String reason) {}

    public record PaymentplanIdResponse(String id) {}

    public record PaymentplanResponse(
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
