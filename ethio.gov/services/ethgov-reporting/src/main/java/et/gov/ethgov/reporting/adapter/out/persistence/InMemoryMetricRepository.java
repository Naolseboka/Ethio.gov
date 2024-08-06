package et.gov.ethgov.reporting.adapter.out.persistence;

import et.gov.ethgov.reporting.domain.metric.Metric;
import et.gov.ethgov.reporting.domain.metric.MetricId;
import et.gov.ethgov.reporting.domain.metric.MetricRepository;
import et.gov.ethgov.reporting.domain.metric.MetricStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Metric (replaceable with JPA). */
public final class InMemoryMetricRepository implements MetricRepository {
    private final Map<UUID, StoredMetric> store = new ConcurrentHashMap<>();

    @Override
    public Metric save(Metric aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredMetric.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Metric> findById(MetricId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredMetric::toDomain);
    }

    @Override
    public Optional<Metric> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredMetric::toDomain)
                .findFirst();
    }

    @Override
    public List<Metric> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredMetric row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredMetric::toDomain)
                .toList();
    }

    @Override
    public void delete(MetricId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredMetric(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredMetric from(Metric aggregate) {
            return new StoredMetric(
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

        Metric toDomain() {
            return Metric.rehydrate(
                    MetricId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    MetricStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
