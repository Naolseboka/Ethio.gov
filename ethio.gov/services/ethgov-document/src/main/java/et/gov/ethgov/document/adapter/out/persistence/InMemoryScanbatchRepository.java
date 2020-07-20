package et.gov.ethgov.document.adapter.out.persistence;

import et.gov.ethgov.document.domain.scanbatch.Scanbatch;
import et.gov.ethgov.document.domain.scanbatch.ScanbatchId;
import et.gov.ethgov.document.domain.scanbatch.ScanbatchRepository;
import et.gov.ethgov.document.domain.scanbatch.ScanbatchStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Scanbatch (replaceable with JPA). */
public final class InMemoryScanbatchRepository implements ScanbatchRepository {
    private final Map<UUID, StoredScanbatch> store = new ConcurrentHashMap<>();

    @Override
    public Scanbatch save(Scanbatch aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredScanbatch.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Scanbatch> findById(ScanbatchId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredScanbatch::toDomain);
    }

    @Override
    public Optional<Scanbatch> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredScanbatch::toDomain)
                .findFirst();
    }

    @Override
    public List<Scanbatch> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredScanbatch row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredScanbatch::toDomain)
                .toList();
    }

    @Override
    public void delete(ScanbatchId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredScanbatch(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredScanbatch from(Scanbatch aggregate) {
            return new StoredScanbatch(
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

        Scanbatch toDomain() {
            return Scanbatch.rehydrate(
                    ScanbatchId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    ScanbatchStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
