package et.gov.ethgov.land.adapter.out.persistence;

import et.gov.ethgov.land.domain.boundary.Boundary;
import et.gov.ethgov.land.domain.boundary.BoundaryId;
import et.gov.ethgov.land.domain.boundary.BoundaryRepository;
import et.gov.ethgov.land.domain.boundary.BoundaryStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Boundary (replaceable with JPA). */
public final class InMemoryBoundaryRepository implements BoundaryRepository {
    private final Map<UUID, StoredBoundary> store = new ConcurrentHashMap<>();

    @Override
    public Boundary save(Boundary aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredBoundary.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Boundary> findById(BoundaryId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredBoundary::toDomain);
    }

    @Override
    public Optional<Boundary> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredBoundary::toDomain)
                .findFirst();
    }

    @Override
    public List<Boundary> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredBoundary row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredBoundary::toDomain)
                .toList();
    }

    @Override
    public void delete(BoundaryId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredBoundary(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredBoundary from(Boundary aggregate) {
            return new StoredBoundary(
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

        Boundary toDomain() {
            return Boundary.rehydrate(
                    BoundaryId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    BoundaryStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
