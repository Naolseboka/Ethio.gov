package et.gov.ethgov.payments.application.paymentplan;

import et.gov.ethgov.payments.domain.paymentplan.Paymentplan;
import et.gov.ethgov.payments.domain.paymentplan.PaymentplanId;
import et.gov.ethgov.payments.domain.paymentplan.PaymentplanRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdatePaymentplanUseCase {
    private final PaymentplanRepository repository;
    private final Clock clock;

    public UpdatePaymentplanUseCase(PaymentplanRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public PaymentplanId execute(UpdatePaymentplanCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        PaymentplanId id = PaymentplanId.parse(command.paymentplanId());
        Paymentplan aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("paymentplan not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdatePaymentplanCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String paymentplanId,
            String reason) {}
}
