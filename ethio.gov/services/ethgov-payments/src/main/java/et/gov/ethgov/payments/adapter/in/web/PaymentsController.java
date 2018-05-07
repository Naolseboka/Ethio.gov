package et.gov.ethgov.payments.adapter.in.web;

import et.gov.ethgov.payments.application.payment.CancelPaymentUseCase;
import et.gov.ethgov.payments.application.payment.FailPaymentUseCase;
import et.gov.ethgov.payments.application.payment.GetPaymentUseCase;
import et.gov.ethgov.payments.application.payment.InitiatePaymentUseCase;
import et.gov.ethgov.payments.application.payment.RefundPaymentUseCase;
import et.gov.ethgov.payments.application.payment.SucceedPaymentUseCase;
import et.gov.ethgov.payments.domain.payment.Payment;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/payments")
public class PaymentsController {

    private final InitiatePaymentUseCase initiatePayment;
    private final SucceedPaymentUseCase succeedPayment;
    private final FailPaymentUseCase failPayment;
    private final CancelPaymentUseCase cancelPayment;
    private final RefundPaymentUseCase refundPayment;
    private final GetPaymentUseCase getPayment;

    public PaymentsController(
            InitiatePaymentUseCase initiatePayment,
            SucceedPaymentUseCase succeedPayment,
            FailPaymentUseCase failPayment,
            CancelPaymentUseCase cancelPayment,
            RefundPaymentUseCase refundPayment,
            GetPaymentUseCase getPayment) {
        this.initiatePayment = initiatePayment;
        this.succeedPayment = succeedPayment;
        this.failPayment = failPayment;
        this.cancelPayment = cancelPayment;
        this.refundPayment = refundPayment;
        this.getPayment = getPayment;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentIdResponse initiate(@Valid @RequestBody InitiatePaymentRequest request) {
        return new PaymentIdResponse(initiatePayment.execute(new InitiatePaymentUseCase.Command(
                        request.tenantId(),
                        request.billId(),
                        request.method(),
                        request.citizenId(),
                        request.organizationId(),
                        request.amountEtb(),
                        request.idempotencyKey()))
                .value());
    }

    @GetMapping("/lookup")
    public PaymentResponse lookup(@RequestParam String tenantId, @RequestParam String paymentNumber) {
        return PaymentResponse.from(getPayment.byNumber(tenantId, paymentNumber));
    }

    @GetMapping("/{paymentId}")
    public PaymentResponse get(@PathVariable String paymentId) {
        return PaymentResponse.from(getPayment.byId(paymentId));
    }

    @PostMapping("/{paymentId}/succeed")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void succeed(@PathVariable String paymentId, @Valid @RequestBody SucceedRequest request) {
        succeedPayment.execute(paymentId, request.providerReference());
    }

    @PostMapping("/{paymentId}/fail")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void fail(@PathVariable String paymentId, @Valid @RequestBody ReasonRequest request) {
        failPayment.execute(paymentId, request.reason());
    }

    @PostMapping("/{paymentId}/cancel")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancel(@PathVariable String paymentId, @Valid @RequestBody ReasonRequest request) {
        cancelPayment.execute(paymentId, request.reason());
    }

    @PostMapping("/{paymentId}/refund")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void refund(@PathVariable String paymentId, @Valid @RequestBody ReasonRequest request) {
        refundPayment.execute(paymentId, request.reason());
    }

    public record InitiatePaymentRequest(
            @NotBlank String tenantId,
            @NotBlank String billId,
            @NotBlank String method,
            String citizenId,
            String organizationId,
            @NotBlank String amountEtb,
            String idempotencyKey) {}

    public record SucceedRequest(@NotBlank @Size(min = 5, max = 120) String providerReference) {}

    public record ReasonRequest(@NotBlank @Size(min = 5, max = 500) String reason) {}

    public record PaymentIdResponse(String paymentId) {}

    public record PaymentResponse(
            String paymentId,
            String tenantId,
            String paymentNumber,
            String billId,
            String method,
            String status,
            String citizenId,
            String organizationId,
            String amountEtb,
            String providerReference,
            String idempotencyKey,
            String initiatedAt,
            String succeededAt) {

        static PaymentResponse from(Payment payment) {
            return new PaymentResponse(
                    payment.id().value(),
                    payment.tenantId().value(),
                    payment.paymentNumber().value(),
                    payment.billId(),
                    payment.method().name(),
                    payment.status().name(),
                    payment.party().citizenId().orElse(null),
                    payment.party().organizationId().orElse(null),
                    payment.amount().amount().toPlainString(),
                    payment.providerReference().orElse(null),
                    payment.idempotencyKey().orElse(null),
                    payment.initiatedAt().toString(),
                    payment.succeededAt().map(java.time.Instant::toString).orElse(null));
        }
    }
}
