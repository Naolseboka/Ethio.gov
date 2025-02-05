package et.gov.ethgov.revenue.application.penalty;

import et.gov.ethgov.revenue.domain.penalty.Penalty;
import et.gov.ethgov.revenue.domain.penalty.PenaltyId;
import et.gov.ethgov.revenue.domain.penalty.PenaltyRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdatePenaltyUseCase {
    private final PenaltyRepository repository;
    private final Clock clock;

    public UpdatePenaltyUseCase(PenaltyRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public PenaltyId execute(UpdatePenaltyCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        PenaltyId id = PenaltyId.parse(command.penaltyId());
        Penalty aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("penalty not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdatePenaltyCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String penaltyId,
            String reason) {}
}
