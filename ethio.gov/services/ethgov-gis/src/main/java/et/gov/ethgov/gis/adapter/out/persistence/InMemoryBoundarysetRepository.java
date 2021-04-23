package et.gov.ethgov.gis.adapter.out.persistence;

import et.gov.ethgov.gis.domain.boundaryset.Boundaryset;
import et.gov.ethgov.gis.domain.boundaryset.BoundarysetId;
import et.gov.ethgov.gis.domain.boundaryset.BoundarysetRepository;
import et.gov.ethgov.gis.domain.boundaryset.BoundarysetStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Boundaryset (replaceable with JPA). */
public final class InMemoryBoundarysetRepository implements BoundarysetRepository {
    private final Map<UUID, StoredBoundaryset> store = new ConcurrentHashMap<>();

    @Override
    public Boundaryset save(Boundaryset aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredBoundaryset.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Boundaryset> findById(BoundarysetId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredBoundaryset::toDomain);
    }

    @Override
    public Optional<Boundaryset> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredBoundaryset::toDomain)
                .findFirst();
    }

    @Override
    public List<Boundaryset> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredBoundaryset row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredBoundaryset::toDomain)
                .toList();
    }

    @Override
    public void delete(BoundarysetId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredBoundaryset(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredBoundaryset from(Boundaryset aggregate) {
            return new StoredBoundaryset(
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

        Boundaryset toDomain() {
            return Boundaryset.rehydrate(
                    BoundarysetId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    BoundarysetStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
