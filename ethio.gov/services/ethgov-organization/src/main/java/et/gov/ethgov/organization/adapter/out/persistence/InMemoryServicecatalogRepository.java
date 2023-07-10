package et.gov.ethgov.organization.adapter.out.persistence;

import et.gov.ethgov.organization.domain.servicecatalog.Servicecatalog;
import et.gov.ethgov.organization.domain.servicecatalog.ServicecatalogId;
import et.gov.ethgov.organization.domain.servicecatalog.ServicecatalogRepository;
import et.gov.ethgov.organization.domain.servicecatalog.ServicecatalogStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Servicecatalog (replaceable with JPA). */
public final class InMemoryServicecatalogRepository implements ServicecatalogRepository {
    private final Map<UUID, StoredServicecatalog> store = new ConcurrentHashMap<>();

    @Override
    public Servicecatalog save(Servicecatalog aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredServicecatalog.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Servicecatalog> findById(ServicecatalogId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredServicecatalog::toDomain);
    }

    @Override
    public Optional<Servicecatalog> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredServicecatalog::toDomain)
                .findFirst();
    }

    @Override
    public List<Servicecatalog> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredServicecatalog row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredServicecatalog::toDomain)
                .toList();
    }

    @Override
    public void delete(ServicecatalogId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredServicecatalog(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredServicecatalog from(Servicecatalog aggregate) {
            return new StoredServicecatalog(
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

        Servicecatalog toDomain() {
            return Servicecatalog.rehydrate(
                    ServicecatalogId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    ServicecatalogStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
