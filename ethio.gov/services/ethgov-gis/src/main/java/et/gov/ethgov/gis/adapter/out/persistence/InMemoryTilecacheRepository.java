package et.gov.ethgov.gis.adapter.out.persistence;

import et.gov.ethgov.gis.domain.tilecache.Tilecache;
import et.gov.ethgov.gis.domain.tilecache.TilecacheId;
import et.gov.ethgov.gis.domain.tilecache.TilecacheRepository;
import et.gov.ethgov.gis.domain.tilecache.TilecacheStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Tilecache (replaceable with JPA). */
public final class InMemoryTilecacheRepository implements TilecacheRepository {
    private final Map<UUID, StoredTilecache> store = new ConcurrentHashMap<>();

    @Override
    public Tilecache save(Tilecache aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredTilecache.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Tilecache> findById(TilecacheId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredTilecache::toDomain);
    }

    @Override
    public Optional<Tilecache> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredTilecache::toDomain)
                .findFirst();
    }

    @Override
    public List<Tilecache> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredTilecache row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredTilecache::toDomain)
                .toList();
    }

    @Override
    public void delete(TilecacheId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredTilecache(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredTilecache from(Tilecache aggregate) {
            return new StoredTilecache(
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

        Tilecache toDomain() {
            return Tilecache.rehydrate(
                    TilecacheId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    TilecacheStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
