package et.gov.ethgov.payments.application.payment;

import et.gov.ethgov.payments.domain.payment.Payment;
import et.gov.ethgov.payments.domain.payment.PaymentId;
import et.gov.ethgov.payments.domain.payment.PaymentRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivatePaymentUseCase {
    private final PaymentRepository repository;
    private final Clock clock;

    public ActivatePaymentUseCase(PaymentRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public PaymentId execute(ActivatePaymentCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        PaymentId id = PaymentId.parse(command.paymentId());
        Payment aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("payment not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivatePaymentCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String paymentId,
            String reason) {}
}
