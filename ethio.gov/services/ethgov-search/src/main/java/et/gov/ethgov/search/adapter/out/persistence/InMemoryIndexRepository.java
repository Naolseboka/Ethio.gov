package et.gov.ethgov.search.adapter.out.persistence;

import et.gov.ethgov.search.domain.index.Index;
import et.gov.ethgov.search.domain.index.IndexId;
import et.gov.ethgov.search.domain.index.IndexRepository;
import et.gov.ethgov.search.domain.index.IndexStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Index (replaceable with JPA). */
public final class InMemoryIndexRepository implements IndexRepository {
    private final Map<UUID, StoredIndex> store = new ConcurrentHashMap<>();

    @Override
    public Index save(Index aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredIndex.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Index> findById(IndexId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredIndex::toDomain);
    }

    @Override
    public Optional<Index> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredIndex::toDomain)
                .findFirst();
    }

    @Override
    public List<Index> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredIndex row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredIndex::toDomain)
                .toList();
    }

    @Override
    public void delete(IndexId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredIndex(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredIndex from(Index aggregate) {
            return new StoredIndex(
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

        Index toDomain() {
            return Index.rehydrate(
                    IndexId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    IndexStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
