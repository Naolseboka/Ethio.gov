package et.gov.ethgov.payments.application.refund;

import et.gov.ethgov.payments.domain.refund.Refund;
import et.gov.ethgov.payments.domain.refund.RefundId;
import et.gov.ethgov.payments.domain.refund.RefundRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateRefundUseCase {
    private final RefundRepository repository;
    private final Clock clock;

    public UpdateRefundUseCase(RefundRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public RefundId execute(UpdateRefundCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        RefundId id = RefundId.parse(command.refundId());
        Refund aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("refund not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateRefundCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String refundId,
            String reason) {}
}
