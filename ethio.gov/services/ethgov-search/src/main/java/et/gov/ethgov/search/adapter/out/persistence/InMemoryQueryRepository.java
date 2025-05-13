package et.gov.ethgov.search.adapter.out.persistence;

import et.gov.ethgov.search.domain.query.Query;
import et.gov.ethgov.search.domain.query.QueryId;
import et.gov.ethgov.search.domain.query.QueryRepository;
import et.gov.ethgov.search.domain.query.QueryStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Query (replaceable with JPA). */
public final class InMemoryQueryRepository implements QueryRepository {
    private final Map<UUID, StoredQuery> store = new ConcurrentHashMap<>();

    @Override
    public Query save(Query aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredQuery.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Query> findById(QueryId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredQuery::toDomain);
    }

    @Override
    public Optional<Query> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredQuery::toDomain)
                .findFirst();
    }

    @Override
    public List<Query> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredQuery row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredQuery::toDomain)
                .toList();
    }

    @Override
    public void delete(QueryId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredQuery(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredQuery from(Query aggregate) {
            return new StoredQuery(
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

        Query toDomain() {
            return Query.rehydrate(
                    QueryId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    QueryStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
