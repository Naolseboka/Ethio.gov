package et.gov.ethgov.directory.adapter.out.persistence;

import et.gov.ethgov.directory.domain.servicedesk.Servicedesk;
import et.gov.ethgov.directory.domain.servicedesk.ServicedeskId;
import et.gov.ethgov.directory.domain.servicedesk.ServicedeskRepository;
import et.gov.ethgov.directory.domain.servicedesk.ServicedeskStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Servicedesk (replaceable with JPA). */
public final class InMemoryServicedeskRepository implements ServicedeskRepository {
    private final Map<UUID, StoredServicedesk> store = new ConcurrentHashMap<>();

    @Override
    public Servicedesk save(Servicedesk aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredServicedesk.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Servicedesk> findById(ServicedeskId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredServicedesk::toDomain);
    }

    @Override
    public Optional<Servicedesk> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredServicedesk::toDomain)
                .findFirst();
    }

    @Override
    public List<Servicedesk> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredServicedesk row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredServicedesk::toDomain)
                .toList();
    }

    @Override
    public void delete(ServicedeskId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredServicedesk(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredServicedesk from(Servicedesk aggregate) {
            return new StoredServicedesk(
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

        Servicedesk toDomain() {
            return Servicedesk.rehydrate(
                    ServicedeskId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    ServicedeskStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
