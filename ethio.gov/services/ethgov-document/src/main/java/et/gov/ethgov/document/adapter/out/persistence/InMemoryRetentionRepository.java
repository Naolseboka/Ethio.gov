package et.gov.ethgov.document.adapter.out.persistence;

import et.gov.ethgov.document.domain.retention.Retention;
import et.gov.ethgov.document.domain.retention.RetentionId;
import et.gov.ethgov.document.domain.retention.RetentionRepository;
import et.gov.ethgov.document.domain.retention.RetentionStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Retention (replaceable with JPA). */
public final class InMemoryRetentionRepository implements RetentionRepository {
    private final Map<UUID, StoredRetention> store = new ConcurrentHashMap<>();

    @Override
    public Retention save(Retention aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredRetention.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Retention> findById(RetentionId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredRetention::toDomain);
    }

    @Override
    public Optional<Retention> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredRetention::toDomain)
                .findFirst();
    }

    @Override
    public List<Retention> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredRetention row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredRetention::toDomain)
                .toList();
    }

    @Override
    public void delete(RetentionId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredRetention(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredRetention from(Retention aggregate) {
            return new StoredRetention(
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

        Retention toDomain() {
            return Retention.rehydrate(
                    RetentionId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    RetentionStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
