package et.gov.ethgov.reporting.domain.snapshot;

import java.util.List;
import java.util.Optional;

public interface SnapshotRepository {
    Snapshot save(Snapshot aggregate);

    Optional<Snapshot> findById(SnapshotId id);

    Optional<Snapshot> findByReferenceCode(String tenantId, String referenceCode);

    List<Snapshot> findByTenant(String tenantId, int limit);

    void delete(SnapshotId id);
}
