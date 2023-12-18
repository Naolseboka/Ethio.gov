package et.gov.ethgov.payments.application.paymentplan;

import et.gov.ethgov.payments.domain.paymentplan.Paymentplan;
import et.gov.ethgov.payments.domain.paymentplan.PaymentplanId;
import et.gov.ethgov.payments.domain.paymentplan.PaymentplanRepository;
import java.util.Objects;

public final class GetPaymentplanUseCase {
    private final PaymentplanRepository repository;

    public GetPaymentplanUseCase(PaymentplanRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Paymentplan execute(String rawId) {
        PaymentplanId id = PaymentplanId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("paymentplan not found: " + rawId));
    }
}
