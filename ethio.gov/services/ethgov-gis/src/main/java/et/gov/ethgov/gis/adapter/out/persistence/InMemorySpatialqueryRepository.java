package et.gov.ethgov.gis.adapter.out.persistence;

import et.gov.ethgov.gis.domain.spatialquery.Spatialquery;
import et.gov.ethgov.gis.domain.spatialquery.SpatialqueryId;
import et.gov.ethgov.gis.domain.spatialquery.SpatialqueryRepository;
import et.gov.ethgov.gis.domain.spatialquery.SpatialqueryStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Spatialquery (replaceable with JPA). */
public final class InMemorySpatialqueryRepository implements SpatialqueryRepository {
    private final Map<UUID, StoredSpatialquery> store = new ConcurrentHashMap<>();

    @Override
    public Spatialquery save(Spatialquery aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredSpatialquery.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Spatialquery> findById(SpatialqueryId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredSpatialquery::toDomain);
    }

    @Override
    public Optional<Spatialquery> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredSpatialquery::toDomain)
                .findFirst();
    }

    @Override
    public List<Spatialquery> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredSpatialquery row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredSpatialquery::toDomain)
                .toList();
    }

    @Override
    public void delete(SpatialqueryId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredSpatialquery(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredSpatialquery from(Spatialquery aggregate) {
            return new StoredSpatialquery(
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

        Spatialquery toDomain() {
            return Spatialquery.rehydrate(
                    SpatialqueryId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    SpatialqueryStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
