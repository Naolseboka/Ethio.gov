package et.gov.ethgov.land.application.conflict;

import et.gov.ethgov.land.domain.conflict.Conflict;
import et.gov.ethgov.land.domain.conflict.ConflictId;
import et.gov.ethgov.land.domain.conflict.ConflictRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendConflictUseCase {
    private final ConflictRepository repository;
    private final Clock clock;

    public SuspendConflictUseCase(ConflictRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public ConflictId execute(SuspendConflictCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        ConflictId id = ConflictId.parse(command.conflictId());
        Conflict aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("conflict not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendConflictCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String conflictId,
            String reason) {}
}
