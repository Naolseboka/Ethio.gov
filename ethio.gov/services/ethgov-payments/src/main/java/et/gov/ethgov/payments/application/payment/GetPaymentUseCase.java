package et.gov.ethgov.payments.application.payment;

import et.gov.ethgov.payments.domain.payment.Payment;
import et.gov.ethgov.payments.domain.payment.PaymentId;
import et.gov.ethgov.payments.domain.payment.PaymentRepository;
import java.util.Objects;

public final class GetPaymentUseCase {
    private final PaymentRepository repository;

    public GetPaymentUseCase(PaymentRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Payment execute(String rawId) {
        PaymentId id = PaymentId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("payment not found: " + rawId));
    }
}
