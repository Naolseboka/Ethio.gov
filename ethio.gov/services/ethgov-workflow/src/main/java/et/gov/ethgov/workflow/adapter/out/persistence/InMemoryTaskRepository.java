package et.gov.ethgov.workflow.adapter.out.persistence;

import et.gov.ethgov.workflow.domain.task.Task;
import et.gov.ethgov.workflow.domain.task.TaskId;
import et.gov.ethgov.workflow.domain.task.TaskRepository;
import et.gov.ethgov.workflow.domain.task.TaskStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Task (replaceable with JPA). */
public final class InMemoryTaskRepository implements TaskRepository {
    private final Map<UUID, StoredTask> store = new ConcurrentHashMap<>();

    @Override
    public Task save(Task aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredTask.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Task> findById(TaskId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredTask::toDomain);
    }

    @Override
    public Optional<Task> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredTask::toDomain)
                .findFirst();
    }

    @Override
    public List<Task> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredTask row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredTask::toDomain)
                .toList();
    }

    @Override
    public void delete(TaskId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredTask(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredTask from(Task aggregate) {
            return new StoredTask(
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

        Task toDomain() {
            return Task.rehydrate(
                    TaskId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    TaskStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
