package et.gov.ethgov.gis.adapter.out.persistence;

import et.gov.ethgov.gis.domain.basemap.Basemap;
import et.gov.ethgov.gis.domain.basemap.BasemapId;
import et.gov.ethgov.gis.domain.basemap.BasemapRepository;
import et.gov.ethgov.gis.domain.basemap.BasemapStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Basemap (replaceable with JPA). */
public final class InMemoryBasemapRepository implements BasemapRepository {
    private final Map<UUID, StoredBasemap> store = new ConcurrentHashMap<>();

    @Override
    public Basemap save(Basemap aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredBasemap.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Basemap> findById(BasemapId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredBasemap::toDomain);
    }

    @Override
    public Optional<Basemap> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredBasemap::toDomain)
                .findFirst();
    }

    @Override
    public List<Basemap> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredBasemap row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredBasemap::toDomain)
                .toList();
    }

    @Override
    public void delete(BasemapId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredBasemap(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredBasemap from(Basemap aggregate) {
            return new StoredBasemap(
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

        Basemap toDomain() {
            return Basemap.rehydrate(
                    BasemapId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    BasemapStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
