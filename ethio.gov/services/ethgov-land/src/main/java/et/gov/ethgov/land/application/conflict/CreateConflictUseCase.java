package et.gov.ethgov.land.application.conflict;

import et.gov.ethgov.land.domain.conflict.Conflict;
import et.gov.ethgov.land.domain.conflict.ConflictId;
import et.gov.ethgov.land.domain.conflict.ConflictRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateConflictUseCase {
    private final ConflictRepository repository;
    private final Clock clock;

    public CreateConflictUseCase(ConflictRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public ConflictId execute(CreateConflictCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Conflict aggregate = Conflict.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateConflictCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String conflictId,
            String reason) {}
}
