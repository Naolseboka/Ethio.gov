package et.gov.ethgov.notification.adapter.out.persistence;

import et.gov.ethgov.notification.domain.suppression.Suppression;
import et.gov.ethgov.notification.domain.suppression.SuppressionId;
import et.gov.ethgov.notification.domain.suppression.SuppressionRepository;
import et.gov.ethgov.notification.domain.suppression.SuppressionStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Suppression (replaceable with JPA). */
public final class InMemorySuppressionRepository implements SuppressionRepository {
    private final Map<UUID, StoredSuppression> store = new ConcurrentHashMap<>();

    @Override
    public Suppression save(Suppression aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredSuppression.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Suppression> findById(SuppressionId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredSuppression::toDomain);
    }

    @Override
    public Optional<Suppression> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredSuppression::toDomain)
                .findFirst();
    }

    @Override
    public List<Suppression> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredSuppression row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredSuppression::toDomain)
                .toList();
    }

    @Override
    public void delete(SuppressionId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredSuppression(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredSuppression from(Suppression aggregate) {
            return new StoredSuppression(
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

        Suppression toDomain() {
            return Suppression.rehydrate(
                    SuppressionId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    SuppressionStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
