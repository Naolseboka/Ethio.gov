package et.gov.ethgov.document.adapter.out.persistence;

import et.gov.ethgov.document.domain.accessgrant.Accessgrant;
import et.gov.ethgov.document.domain.accessgrant.AccessgrantId;
import et.gov.ethgov.document.domain.accessgrant.AccessgrantRepository;
import et.gov.ethgov.document.domain.accessgrant.AccessgrantStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Accessgrant (replaceable with JPA). */
public final class InMemoryAccessgrantRepository implements AccessgrantRepository {
    private final Map<UUID, StoredAccessgrant> store = new ConcurrentHashMap<>();

    @Override
    public Accessgrant save(Accessgrant aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredAccessgrant.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Accessgrant> findById(AccessgrantId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredAccessgrant::toDomain);
    }

    @Override
    public Optional<Accessgrant> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredAccessgrant::toDomain)
                .findFirst();
    }

    @Override
    public List<Accessgrant> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredAccessgrant row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredAccessgrant::toDomain)
                .toList();
    }

    @Override
    public void delete(AccessgrantId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredAccessgrant(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredAccessgrant from(Accessgrant aggregate) {
            return new StoredAccessgrant(
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

        Accessgrant toDomain() {
            return Accessgrant.rehydrate(
                    AccessgrantId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    AccessgrantStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
