package et.gov.ethgov.permits.adapter.out.persistence;

import et.gov.ethgov.permits.domain.permitcondition.Permitcondition;
import et.gov.ethgov.permits.domain.permitcondition.PermitconditionId;
import et.gov.ethgov.permits.domain.permitcondition.PermitconditionRepository;
import et.gov.ethgov.permits.domain.permitcondition.PermitconditionStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Permitcondition (replaceable with JPA). */
public final class InMemoryPermitconditionRepository implements PermitconditionRepository {
    private final Map<UUID, StoredPermitcondition> store = new ConcurrentHashMap<>();

    @Override
    public Permitcondition save(Permitcondition aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredPermitcondition.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Permitcondition> findById(PermitconditionId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredPermitcondition::toDomain);
    }

    @Override
    public Optional<Permitcondition> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredPermitcondition::toDomain)
                .findFirst();
    }

    @Override
    public List<Permitcondition> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredPermitcondition row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredPermitcondition::toDomain)
                .toList();
    }

    @Override
    public void delete(PermitconditionId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredPermitcondition(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredPermitcondition from(Permitcondition aggregate) {
            return new StoredPermitcondition(
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

        Permitcondition toDomain() {
            return Permitcondition.rehydrate(
                    PermitconditionId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    PermitconditionStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
