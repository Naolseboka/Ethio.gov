package et.gov.ethgov.permits.adapter.out.persistence;

import et.gov.ethgov.permits.domain.permitapplication.Permitapplication;
import et.gov.ethgov.permits.domain.permitapplication.PermitapplicationId;
import et.gov.ethgov.permits.domain.permitapplication.PermitapplicationRepository;
import et.gov.ethgov.permits.domain.permitapplication.PermitapplicationStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Permitapplication (replaceable with JPA). */
public final class InMemoryPermitapplicationRepository implements PermitapplicationRepository {
    private final Map<UUID, StoredPermitapplication> store = new ConcurrentHashMap<>();

    @Override
    public Permitapplication save(Permitapplication aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredPermitapplication.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Permitapplication> findById(PermitapplicationId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredPermitapplication::toDomain);
    }

    @Override
    public Optional<Permitapplication> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredPermitapplication::toDomain)
                .findFirst();
    }

    @Override
    public List<Permitapplication> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredPermitapplication row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredPermitapplication::toDomain)
                .toList();
    }

    @Override
    public void delete(PermitapplicationId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredPermitapplication(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredPermitapplication from(Permitapplication aggregate) {
            return new StoredPermitapplication(
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

        Permitapplication toDomain() {
            return Permitapplication.rehydrate(
                    PermitapplicationId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    PermitapplicationStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
