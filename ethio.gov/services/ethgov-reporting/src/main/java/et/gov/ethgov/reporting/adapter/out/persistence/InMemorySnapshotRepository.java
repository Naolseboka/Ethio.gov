package et.gov.ethgov.reporting.adapter.out.persistence;

import et.gov.ethgov.reporting.domain.snapshot.Snapshot;
import et.gov.ethgov.reporting.domain.snapshot.SnapshotId;
import et.gov.ethgov.reporting.domain.snapshot.SnapshotRepository;
import et.gov.ethgov.reporting.domain.snapshot.SnapshotStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Snapshot (replaceable with JPA). */
public final class InMemorySnapshotRepository implements SnapshotRepository {
    private final Map<UUID, StoredSnapshot> store = new ConcurrentHashMap<>();

    @Override
    public Snapshot save(Snapshot aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredSnapshot.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Snapshot> findById(SnapshotId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredSnapshot::toDomain);
    }

    @Override
    public Optional<Snapshot> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredSnapshot::toDomain)
                .findFirst();
    }

    @Override
    public List<Snapshot> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredSnapshot row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredSnapshot::toDomain)
                .toList();
    }

    @Override
    public void delete(SnapshotId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredSnapshot(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredSnapshot from(Snapshot aggregate) {
            return new StoredSnapshot(
                    aggregate.id().value(),
                    aggregate.referenceCode(),
                    aggregate.title(),
                    aggregate.description(),
                    aggregate.tenantId(),
                    aggregate.status().name(),
                    aggregate.createdAt(),
                    aggregate.updatedAt(),
                    aggregate.version());
        }

        Snapshot toDomain() {
            return Snapshot.rehydrate(
                    SnapshotId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    SnapshotStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
