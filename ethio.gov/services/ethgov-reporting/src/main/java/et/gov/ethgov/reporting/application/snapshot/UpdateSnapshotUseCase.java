package et.gov.ethgov.reporting.application.snapshot;

import et.gov.ethgov.reporting.domain.snapshot.Snapshot;
import et.gov.ethgov.reporting.domain.snapshot.SnapshotId;
import et.gov.ethgov.reporting.domain.snapshot.SnapshotRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateSnapshotUseCase {
    private final SnapshotRepository repository;
    private final Clock clock;

    public UpdateSnapshotUseCase(SnapshotRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public SnapshotId execute(UpdateSnapshotCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        SnapshotId id = SnapshotId.parse(command.snapshotId());
        Snapshot aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("snapshot not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateSnapshotCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String snapshotId,
            String reason) {}
}
