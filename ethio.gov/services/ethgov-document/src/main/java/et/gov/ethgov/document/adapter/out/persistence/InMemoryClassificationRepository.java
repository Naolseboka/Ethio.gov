package et.gov.ethgov.document.adapter.out.persistence;

import et.gov.ethgov.document.domain.classification.Classification;
import et.gov.ethgov.document.domain.classification.ClassificationId;
import et.gov.ethgov.document.domain.classification.ClassificationRepository;
import et.gov.ethgov.document.domain.classification.ClassificationStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Classification (replaceable with JPA). */
public final class InMemoryClassificationRepository implements ClassificationRepository {
    private final Map<UUID, StoredClassification> store = new ConcurrentHashMap<>();

    @Override
    public Classification save(Classification aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredClassification.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Classification> findById(ClassificationId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredClassification::toDomain);
    }

    @Override
    public Optional<Classification> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredClassification::toDomain)
                .findFirst();
    }

    @Override
    public List<Classification> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredClassification row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredClassification::toDomain)
                .toList();
    }

    @Override
    public void delete(ClassificationId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredClassification(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredClassification from(Classification aggregate) {
            return new StoredClassification(
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

        Classification toDomain() {
            return Classification.rehydrate(
                    ClassificationId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    ClassificationStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
