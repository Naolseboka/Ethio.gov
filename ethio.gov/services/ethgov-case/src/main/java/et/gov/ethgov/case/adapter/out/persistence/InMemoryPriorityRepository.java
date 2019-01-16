package et.gov.ethgov.case.adapter.out.persistence;

import et.gov.ethgov.case.domain.priority.Priority;
import et.gov.ethgov.case.domain.priority.PriorityId;
import et.gov.ethgov.case.domain.priority.PriorityRepository;
import et.gov.ethgov.case.domain.priority.PriorityStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Priority (replaceable with JPA). */
public final class InMemoryPriorityRepository implements PriorityRepository {
    private final Map<UUID, StoredPriority> store = new ConcurrentHashMap<>();

    @Override
    public Priority save(Priority aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredPriority.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Priority> findById(PriorityId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredPriority::toDomain);
    }

    @Override
    public Optional<Priority> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredPriority::toDomain)
                .findFirst();
    }

    @Override
    public List<Priority> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredPriority row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredPriority::toDomain)
                .toList();
    }

    @Override
    public void delete(PriorityId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredPriority(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredPriority from(Priority aggregate) {
            return new StoredPriority(
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

        Priority toDomain() {
            return Priority.rehydrate(
                    PriorityId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    PriorityStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
