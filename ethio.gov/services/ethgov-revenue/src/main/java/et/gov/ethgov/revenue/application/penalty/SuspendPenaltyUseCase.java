package et.gov.ethgov.revenue.application.penalty;

import et.gov.ethgov.revenue.domain.penalty.Penalty;
import et.gov.ethgov.revenue.domain.penalty.PenaltyId;
import et.gov.ethgov.revenue.domain.penalty.PenaltyRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendPenaltyUseCase {
    private final PenaltyRepository repository;
    private final Clock clock;

    public SuspendPenaltyUseCase(PenaltyRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public PenaltyId execute(SuspendPenaltyCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        PenaltyId id = PenaltyId.parse(command.penaltyId());
        Penalty aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("penalty not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendPenaltyCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String penaltyId,
            String reason) {}
}
