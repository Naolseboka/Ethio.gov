package et.gov.ethgov.payments.application.payment;

import et.gov.ethgov.payments.domain.payment.Payment;
import et.gov.ethgov.payments.domain.payment.PaymentId;
import et.gov.ethgov.payments.domain.payment.PaymentRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreatePaymentUseCase {
    private final PaymentRepository repository;
    private final Clock clock;

    public CreatePaymentUseCase(PaymentRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public PaymentId execute(CreatePaymentCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Payment aggregate = Payment.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreatePaymentCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String paymentId,
            String reason) {}
}
