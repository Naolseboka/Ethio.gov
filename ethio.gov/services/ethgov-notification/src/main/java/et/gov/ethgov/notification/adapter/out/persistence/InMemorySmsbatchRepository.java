package et.gov.ethgov.notification.adapter.out.persistence;

import et.gov.ethgov.notification.domain.smsbatch.Smsbatch;
import et.gov.ethgov.notification.domain.smsbatch.SmsbatchId;
import et.gov.ethgov.notification.domain.smsbatch.SmsbatchRepository;
import et.gov.ethgov.notification.domain.smsbatch.SmsbatchStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Smsbatch (replaceable with JPA). */
public final class InMemorySmsbatchRepository implements SmsbatchRepository {
    private final Map<UUID, StoredSmsbatch> store = new ConcurrentHashMap<>();

    @Override
    public Smsbatch save(Smsbatch aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredSmsbatch.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Smsbatch> findById(SmsbatchId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredSmsbatch::toDomain);
    }

    @Override
    public Optional<Smsbatch> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredSmsbatch::toDomain)
                .findFirst();
    }

    @Override
    public List<Smsbatch> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredSmsbatch row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredSmsbatch::toDomain)
                .toList();
    }

    @Override
    public void delete(SmsbatchId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredSmsbatch(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredSmsbatch from(Smsbatch aggregate) {
            return new StoredSmsbatch(
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

        Smsbatch toDomain() {
            return Smsbatch.rehydrate(
                    SmsbatchId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    SmsbatchStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
