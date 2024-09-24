package et.gov.ethgov.reporting.application.snapshot;

import et.gov.ethgov.reporting.domain.snapshot.Snapshot;
import et.gov.ethgov.reporting.domain.snapshot.SnapshotId;
import et.gov.ethgov.reporting.domain.snapshot.SnapshotRepository;
import java.util.Objects;

public final class GetSnapshotUseCase {
    private final SnapshotRepository repository;

    public GetSnapshotUseCase(SnapshotRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Snapshot execute(String rawId) {
        SnapshotId id = SnapshotId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("snapshot not found: " + rawId));
    }
}
