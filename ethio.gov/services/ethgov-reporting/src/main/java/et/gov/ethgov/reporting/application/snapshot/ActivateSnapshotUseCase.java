package et.gov.ethgov.reporting.application.snapshot;

import et.gov.ethgov.reporting.domain.snapshot.Snapshot;
import et.gov.ethgov.reporting.domain.snapshot.SnapshotId;
import et.gov.ethgov.reporting.domain.snapshot.SnapshotRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivateSnapshotUseCase {
    private final SnapshotRepository repository;
    private final Clock clock;

    public ActivateSnapshotUseCase(SnapshotRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public SnapshotId execute(ActivateSnapshotCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        SnapshotId id = SnapshotId.parse(command.snapshotId());
        Snapshot aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("snapshot not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivateSnapshotCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String snapshotId,
            String reason) {}
}
