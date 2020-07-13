package et.gov.ethgov.document.adapter.out.persistence;

import et.gov.ethgov.document.domain.documentset.Documentset;
import et.gov.ethgov.document.domain.documentset.DocumentsetId;
import et.gov.ethgov.document.domain.documentset.DocumentsetRepository;
import et.gov.ethgov.document.domain.documentset.DocumentsetStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Documentset (replaceable with JPA). */
public final class InMemoryDocumentsetRepository implements DocumentsetRepository {
    private final Map<UUID, StoredDocumentset> store = new ConcurrentHashMap<>();

    @Override
    public Documentset save(Documentset aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredDocumentset.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Documentset> findById(DocumentsetId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredDocumentset::toDomain);
    }

    @Override
    public Optional<Documentset> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredDocumentset::toDomain)
                .findFirst();
    }

    @Override
    public List<Documentset> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredDocumentset row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredDocumentset::toDomain)
                .toList();
    }

    @Override
    public void delete(DocumentsetId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredDocumentset(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredDocumentset from(Documentset aggregate) {
            return new StoredDocumentset(
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

        Documentset toDomain() {
            return Documentset.rehydrate(
                    DocumentsetId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    DocumentsetStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
