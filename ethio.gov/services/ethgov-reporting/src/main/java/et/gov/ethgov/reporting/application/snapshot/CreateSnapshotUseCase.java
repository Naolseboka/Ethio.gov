package et.gov.ethgov.reporting.application.snapshot;

import et.gov.ethgov.reporting.domain.snapshot.Snapshot;
import et.gov.ethgov.reporting.domain.snapshot.SnapshotId;
import et.gov.ethgov.reporting.domain.snapshot.SnapshotRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateSnapshotUseCase {
    private final SnapshotRepository repository;
    private final Clock clock;

    public CreateSnapshotUseCase(SnapshotRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public SnapshotId execute(CreateSnapshotCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Snapshot aggregate = Snapshot.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateSnapshotCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String snapshotId,
            String reason) {}
}
