package et.gov.ethgov.analytics.adapter.out.persistence;

import et.gov.ethgov.analytics.domain.anomaly.Anomaly;
import et.gov.ethgov.analytics.domain.anomaly.AnomalyId;
import et.gov.ethgov.analytics.domain.anomaly.AnomalyRepository;
import et.gov.ethgov.analytics.domain.anomaly.AnomalyStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Anomaly (replaceable with JPA). */
public final class InMemoryAnomalyRepository implements AnomalyRepository {
    private final Map<UUID, StoredAnomaly> store = new ConcurrentHashMap<>();

    @Override
    public Anomaly save(Anomaly aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredAnomaly.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Anomaly> findById(AnomalyId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredAnomaly::toDomain);
    }

    @Override
    public Optional<Anomaly> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredAnomaly::toDomain)
                .findFirst();
    }

    @Override
    public List<Anomaly> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredAnomaly row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredAnomaly::toDomain)
                .toList();
    }

    @Override
    public void delete(AnomalyId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredAnomaly(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredAnomaly from(Anomaly aggregate) {
            return new StoredAnomaly(
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

        Anomaly toDomain() {
            return Anomaly.rehydrate(
                    AnomalyId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    AnomalyStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
