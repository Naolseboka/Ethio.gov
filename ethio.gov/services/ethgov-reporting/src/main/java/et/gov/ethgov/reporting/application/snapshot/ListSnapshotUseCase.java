package et.gov.ethgov.reporting.application.snapshot;

import et.gov.ethgov.reporting.domain.snapshot.Snapshot;
import et.gov.ethgov.reporting.domain.snapshot.SnapshotRepository;
import java.util.List;
import java.util.Objects;

public final class ListSnapshotUseCase {
    private final SnapshotRepository repository;

    public ListSnapshotUseCase(SnapshotRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Snapshot> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
