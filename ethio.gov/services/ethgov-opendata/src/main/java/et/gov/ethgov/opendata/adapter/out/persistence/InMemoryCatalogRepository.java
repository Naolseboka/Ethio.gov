package et.gov.ethgov.opendata.adapter.out.persistence;

import et.gov.ethgov.opendata.domain.catalog.Catalog;
import et.gov.ethgov.opendata.domain.catalog.CatalogId;
import et.gov.ethgov.opendata.domain.catalog.CatalogRepository;
import et.gov.ethgov.opendata.domain.catalog.CatalogStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Catalog (replaceable with JPA). */
public final class InMemoryCatalogRepository implements CatalogRepository {
    private final Map<UUID, StoredCatalog> store = new ConcurrentHashMap<>();

    @Override
    public Catalog save(Catalog aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredCatalog.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Catalog> findById(CatalogId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredCatalog::toDomain);
    }

    @Override
    public Optional<Catalog> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredCatalog::toDomain)
                .findFirst();
    }

    @Override
    public List<Catalog> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredCatalog row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredCatalog::toDomain)
                .toList();
    }

    @Override
    public void delete(CatalogId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredCatalog(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredCatalog from(Catalog aggregate) {
            return new StoredCatalog(
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

        Catalog toDomain() {
            return Catalog.rehydrate(
                    CatalogId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    CatalogStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
