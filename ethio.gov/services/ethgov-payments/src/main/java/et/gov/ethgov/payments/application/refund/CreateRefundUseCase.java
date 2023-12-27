package et.gov.ethgov.payments.application.refund;

import et.gov.ethgov.payments.domain.refund.Refund;
import et.gov.ethgov.payments.domain.refund.RefundId;
import et.gov.ethgov.payments.domain.refund.RefundRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateRefundUseCase {
    private final RefundRepository repository;
    private final Clock clock;

    public CreateRefundUseCase(RefundRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public RefundId execute(CreateRefundCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Refund aggregate = Refund.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateRefundCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String refundId,
            String reason) {}
}
