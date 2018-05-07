package et.gov.ethgov.payments.adapter.in.web;

import et.gov.ethgov.payments.application.payment.ActivatePaymentUseCase;
import et.gov.ethgov.payments.application.payment.CreatePaymentUseCase;
import et.gov.ethgov.payments.application.payment.GetPaymentUseCase;
import et.gov.ethgov.payments.application.payment.ListPaymentUseCase;
import et.gov.ethgov.payments.application.payment.SuspendPaymentUseCase;
import et.gov.ethgov.payments.application.payment.UpdatePaymentUseCase;
import et.gov.ethgov.payments.domain.payment.Payment;
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
@RequestMapping("/v1/payments/payment")
public class PaymentController {
    private final CreatePaymentUseCase createUseCase;
    private final UpdatePaymentUseCase updateUseCase;
    private final ActivatePaymentUseCase activateUseCase;
    private final SuspendPaymentUseCase suspendUseCase;
    private final GetPaymentUseCase getUseCase;
    private final ListPaymentUseCase listUseCase;

    public PaymentController(
            CreatePaymentUseCase createUseCase,
            UpdatePaymentUseCase updateUseCase,
            ActivatePaymentUseCase activateUseCase,
            SuspendPaymentUseCase suspendUseCase,
            GetPaymentUseCase getUseCase,
            ListPaymentUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentIdResponse create(@Valid @RequestBody CreatePaymentRequest request) {
        var id = createUseCase.execute(new CreatePaymentUseCase.CreatePaymentCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new PaymentIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public PaymentResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<PaymentResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public PaymentIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdatePaymentRequest request) {
        var updated = updateUseCase.execute(new UpdatePaymentUseCase.UpdatePaymentCommand(
                null, request.title(), request.description(), null, id, null));
        return new PaymentIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivatePaymentUseCase.ActivatePaymentCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendPaymentRequest request) {
        suspendUseCase.execute(new SuspendPaymentUseCase.SuspendPaymentCommand(
                null, null, null, null, id, request.reason()));
    }

    private PaymentResponse toResponse(Payment aggregate) {
        return new PaymentResponse(
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

    public record CreatePaymentRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdatePaymentRequest(String title, String description) {}

    public record SuspendPaymentRequest(@NotBlank String reason) {}

    public record PaymentIdResponse(String id) {}

    public record PaymentResponse(
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
