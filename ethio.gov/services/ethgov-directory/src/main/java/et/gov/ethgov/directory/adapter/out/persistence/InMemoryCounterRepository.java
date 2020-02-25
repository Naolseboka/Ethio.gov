package et.gov.ethgov.directory.adapter.out.persistence;

import et.gov.ethgov.directory.domain.counter.Counter;
import et.gov.ethgov.directory.domain.counter.CounterId;
import et.gov.ethgov.directory.domain.counter.CounterRepository;
import et.gov.ethgov.directory.domain.counter.CounterStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Counter (replaceable with JPA). */
public final class InMemoryCounterRepository implements CounterRepository {
    private final Map<UUID, StoredCounter> store = new ConcurrentHashMap<>();

    @Override
    public Counter save(Counter aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredCounter.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Counter> findById(CounterId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredCounter::toDomain);
    }

    @Override
    public Optional<Counter> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredCounter::toDomain)
                .findFirst();
    }

    @Override
    public List<Counter> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredCounter row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredCounter::toDomain)
                .toList();
    }

    @Override
    public void delete(CounterId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredCounter(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredCounter from(Counter aggregate) {
            return new StoredCounter(
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

        Counter toDomain() {
            return Counter.rehydrate(
                    CounterId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    CounterStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
