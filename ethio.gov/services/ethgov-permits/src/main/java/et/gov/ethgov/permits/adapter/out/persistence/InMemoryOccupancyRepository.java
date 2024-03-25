package et.gov.ethgov.permits.adapter.out.persistence;

import et.gov.ethgov.permits.domain.occupancy.Occupancy;
import et.gov.ethgov.permits.domain.occupancy.OccupancyId;
import et.gov.ethgov.permits.domain.occupancy.OccupancyRepository;
import et.gov.ethgov.permits.domain.occupancy.OccupancyStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Occupancy (replaceable with JPA). */
public final class InMemoryOccupancyRepository implements OccupancyRepository {
    private final Map<UUID, StoredOccupancy> store = new ConcurrentHashMap<>();

    @Override
    public Occupancy save(Occupancy aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredOccupancy.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Occupancy> findById(OccupancyId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredOccupancy::toDomain);
    }

    @Override
    public Optional<Occupancy> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredOccupancy::toDomain)
                .findFirst();
    }

    @Override
    public List<Occupancy> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredOccupancy row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredOccupancy::toDomain)
                .toList();
    }

    @Override
    public void delete(OccupancyId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredOccupancy(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredOccupancy from(Occupancy aggregate) {
            return new StoredOccupancy(
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

        Occupancy toDomain() {
            return Occupancy.rehydrate(
                    OccupancyId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    OccupancyStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
