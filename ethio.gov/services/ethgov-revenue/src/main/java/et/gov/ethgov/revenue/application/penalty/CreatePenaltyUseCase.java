package et.gov.ethgov.revenue.application.penalty;

import et.gov.ethgov.revenue.domain.penalty.Penalty;
import et.gov.ethgov.revenue.domain.penalty.PenaltyId;
import et.gov.ethgov.revenue.domain.penalty.PenaltyRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreatePenaltyUseCase {
    private final PenaltyRepository repository;
    private final Clock clock;

    public CreatePenaltyUseCase(PenaltyRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public PenaltyId execute(CreatePenaltyCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Penalty aggregate = Penalty.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreatePenaltyCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String penaltyId,
            String reason) {}
}
