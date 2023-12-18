package et.gov.ethgov.payments.application.paymentplan;

import et.gov.ethgov.payments.domain.paymentplan.Paymentplan;
import et.gov.ethgov.payments.domain.paymentplan.PaymentplanId;
import et.gov.ethgov.payments.domain.paymentplan.PaymentplanRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendPaymentplanUseCase {
    private final PaymentplanRepository repository;
    private final Clock clock;

    public SuspendPaymentplanUseCase(PaymentplanRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public PaymentplanId execute(SuspendPaymentplanCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        PaymentplanId id = PaymentplanId.parse(command.paymentplanId());
        Paymentplan aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("paymentplan not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendPaymentplanCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String paymentplanId,
            String reason) {}
}
