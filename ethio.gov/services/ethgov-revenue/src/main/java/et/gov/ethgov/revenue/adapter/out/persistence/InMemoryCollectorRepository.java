package et.gov.ethgov.revenue.adapter.out.persistence;

import et.gov.ethgov.revenue.domain.collector.Collector;
import et.gov.ethgov.revenue.domain.collector.CollectorId;
import et.gov.ethgov.revenue.domain.collector.CollectorRepository;
import et.gov.ethgov.revenue.domain.collector.CollectorStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Collector (replaceable with JPA). */
public final class InMemoryCollectorRepository implements CollectorRepository {
    private final Map<UUID, StoredCollector> store = new ConcurrentHashMap<>();

    @Override
    public Collector save(Collector aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredCollector.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Collector> findById(CollectorId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredCollector::toDomain);
    }

    @Override
    public Optional<Collector> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredCollector::toDomain)
                .findFirst();
    }

    @Override
    public List<Collector> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredCollector row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredCollector::toDomain)
                .toList();
    }

    @Override
    public void delete(CollectorId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredCollector(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredCollector from(Collector aggregate) {
            return new StoredCollector(
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

        Collector toDomain() {
            return Collector.rehydrate(
                    CollectorId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    CollectorStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
