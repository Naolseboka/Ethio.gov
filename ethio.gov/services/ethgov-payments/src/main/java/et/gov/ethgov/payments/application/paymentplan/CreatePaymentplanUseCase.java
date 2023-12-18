package et.gov.ethgov.payments.application.paymentplan;

import et.gov.ethgov.payments.domain.paymentplan.Paymentplan;
import et.gov.ethgov.payments.domain.paymentplan.PaymentplanId;
import et.gov.ethgov.payments.domain.paymentplan.PaymentplanRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreatePaymentplanUseCase {
    private final PaymentplanRepository repository;
    private final Clock clock;

    public CreatePaymentplanUseCase(PaymentplanRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public PaymentplanId execute(CreatePaymentplanCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Paymentplan aggregate = Paymentplan.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreatePaymentplanCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String paymentplanId,
            String reason) {}
}
