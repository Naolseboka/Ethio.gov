package et.gov.ethgov.directory.adapter.out.persistence;

import et.gov.ethgov.directory.domain.queue.Queue;
import et.gov.ethgov.directory.domain.queue.QueueId;
import et.gov.ethgov.directory.domain.queue.QueueRepository;
import et.gov.ethgov.directory.domain.queue.QueueStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Queue (replaceable with JPA). */
public final class InMemoryQueueRepository implements QueueRepository {
    private final Map<UUID, StoredQueue> store = new ConcurrentHashMap<>();

    @Override
    public Queue save(Queue aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredQueue.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Queue> findById(QueueId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredQueue::toDomain);
    }

    @Override
    public Optional<Queue> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredQueue::toDomain)
                .findFirst();
    }

    @Override
    public List<Queue> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredQueue row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredQueue::toDomain)
                .toList();
    }

    @Override
    public void delete(QueueId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredQueue(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredQueue from(Queue aggregate) {
            return new StoredQueue(
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

        Queue toDomain() {
            return Queue.rehydrate(
                    QueueId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    QueueStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
