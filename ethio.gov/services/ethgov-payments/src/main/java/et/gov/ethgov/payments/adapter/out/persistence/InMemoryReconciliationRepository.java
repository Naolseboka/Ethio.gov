package et.gov.ethgov.payments.adapter.out.persistence;

import et.gov.ethgov.payments.domain.reconciliation.Reconciliation;
import et.gov.ethgov.payments.domain.reconciliation.ReconciliationId;
import et.gov.ethgov.payments.domain.reconciliation.ReconciliationRepository;
import et.gov.ethgov.payments.domain.reconciliation.ReconciliationStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Reconciliation (replaceable with JPA). */
public final class InMemoryReconciliationRepository implements ReconciliationRepository {
    private final Map<UUID, StoredReconciliation> store = new ConcurrentHashMap<>();

    @Override
    public Reconciliation save(Reconciliation aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredReconciliation.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Reconciliation> findById(ReconciliationId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredReconciliation::toDomain);
    }

    @Override
    public Optional<Reconciliation> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredReconciliation::toDomain)
                .findFirst();
    }

    @Override
    public List<Reconciliation> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredReconciliation row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredReconciliation::toDomain)
                .toList();
    }

    @Override
    public void delete(ReconciliationId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredReconciliation(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredReconciliation from(Reconciliation aggregate) {
            return new StoredReconciliation(
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

        Reconciliation toDomain() {
            return Reconciliation.rehydrate(
                    ReconciliationId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    ReconciliationStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
