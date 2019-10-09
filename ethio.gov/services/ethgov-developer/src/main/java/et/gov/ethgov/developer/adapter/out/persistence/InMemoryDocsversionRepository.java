package et.gov.ethgov.developer.adapter.out.persistence;

import et.gov.ethgov.developer.domain.docsversion.Docsversion;
import et.gov.ethgov.developer.domain.docsversion.DocsversionId;
import et.gov.ethgov.developer.domain.docsversion.DocsversionRepository;
import et.gov.ethgov.developer.domain.docsversion.DocsversionStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Docsversion (replaceable with JPA). */
public final class InMemoryDocsversionRepository implements DocsversionRepository {
    private final Map<UUID, StoredDocsversion> store = new ConcurrentHashMap<>();

    @Override
    public Docsversion save(Docsversion aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredDocsversion.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Docsversion> findById(DocsversionId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredDocsversion::toDomain);
    }

    @Override
    public Optional<Docsversion> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredDocsversion::toDomain)
                .findFirst();
    }

    @Override
    public List<Docsversion> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredDocsversion row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredDocsversion::toDomain)
                .toList();
    }

    @Override
    public void delete(DocsversionId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredDocsversion(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredDocsversion from(Docsversion aggregate) {
            return new StoredDocsversion(
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

        Docsversion toDomain() {
            return Docsversion.rehydrate(
                    DocsversionId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    DocsversionStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
