package et.gov.ethgov.payments.application.refund;

import et.gov.ethgov.payments.domain.refund.Refund;
import et.gov.ethgov.payments.domain.refund.RefundId;
import et.gov.ethgov.payments.domain.refund.RefundRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendRefundUseCase {
    private final RefundRepository repository;
    private final Clock clock;

    public SuspendRefundUseCase(RefundRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public RefundId execute(SuspendRefundCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        RefundId id = RefundId.parse(command.refundId());
        Refund aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("refund not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendRefundCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String refundId,
            String reason) {}
}
