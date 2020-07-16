package et.gov.ethgov.document.adapter.out.persistence;

import et.gov.ethgov.document.domain.redaction.Redaction;
import et.gov.ethgov.document.domain.redaction.RedactionId;
import et.gov.ethgov.document.domain.redaction.RedactionRepository;
import et.gov.ethgov.document.domain.redaction.RedactionStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Redaction (replaceable with JPA). */
public final class InMemoryRedactionRepository implements RedactionRepository {
    private final Map<UUID, StoredRedaction> store = new ConcurrentHashMap<>();

    @Override
    public Redaction save(Redaction aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredRedaction.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Redaction> findById(RedactionId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredRedaction::toDomain);
    }

    @Override
    public Optional<Redaction> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredRedaction::toDomain)
                .findFirst();
    }

    @Override
    public List<Redaction> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredRedaction row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredRedaction::toDomain)
                .toList();
    }

    @Override
    public void delete(RedactionId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredRedaction(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredRedaction from(Redaction aggregate) {
            return new StoredRedaction(
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

        Redaction toDomain() {
            return Redaction.rehydrate(
                    RedactionId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    RedactionStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
