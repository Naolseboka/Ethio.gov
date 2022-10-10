package et.gov.ethgov.notification.adapter.out.persistence;

import et.gov.ethgov.notification.domain.dispatch.Dispatch;
import et.gov.ethgov.notification.domain.dispatch.DispatchId;
import et.gov.ethgov.notification.domain.dispatch.DispatchRepository;
import et.gov.ethgov.notification.domain.dispatch.DispatchStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Dispatch (replaceable with JPA). */
public final class InMemoryDispatchRepository implements DispatchRepository {
    private final Map<UUID, StoredDispatch> store = new ConcurrentHashMap<>();

    @Override
    public Dispatch save(Dispatch aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredDispatch.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Dispatch> findById(DispatchId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredDispatch::toDomain);
    }

    @Override
    public Optional<Dispatch> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredDispatch::toDomain)
                .findFirst();
    }

    @Override
    public List<Dispatch> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredDispatch row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredDispatch::toDomain)
                .toList();
    }

    @Override
    public void delete(DispatchId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredDispatch(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredDispatch from(Dispatch aggregate) {
            return new StoredDispatch(
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

        Dispatch toDomain() {
            return Dispatch.rehydrate(
                    DispatchId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    DispatchStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
