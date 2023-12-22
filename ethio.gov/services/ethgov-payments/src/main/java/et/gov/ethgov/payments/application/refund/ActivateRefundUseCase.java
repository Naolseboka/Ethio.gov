package et.gov.ethgov.payments.application.refund;

import et.gov.ethgov.payments.domain.refund.Refund;
import et.gov.ethgov.payments.domain.refund.RefundId;
import et.gov.ethgov.payments.domain.refund.RefundRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivateRefundUseCase {
    private final RefundRepository repository;
    private final Clock clock;

    public ActivateRefundUseCase(RefundRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public RefundId execute(ActivateRefundCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        RefundId id = RefundId.parse(command.refundId());
        Refund aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("refund not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivateRefundCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String refundId,
            String reason) {}
}
