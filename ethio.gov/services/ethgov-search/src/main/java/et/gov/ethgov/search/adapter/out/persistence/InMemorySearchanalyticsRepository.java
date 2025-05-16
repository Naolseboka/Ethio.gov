package et.gov.ethgov.search.adapter.out.persistence;

import et.gov.ethgov.search.domain.searchanalytics.Searchanalytics;
import et.gov.ethgov.search.domain.searchanalytics.SearchanalyticsId;
import et.gov.ethgov.search.domain.searchanalytics.SearchanalyticsRepository;
import et.gov.ethgov.search.domain.searchanalytics.SearchanalyticsStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Searchanalytics (replaceable with JPA). */
public final class InMemorySearchanalyticsRepository implements SearchanalyticsRepository {
    private final Map<UUID, StoredSearchanalytics> store = new ConcurrentHashMap<>();

    @Override
    public Searchanalytics save(Searchanalytics aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredSearchanalytics.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Searchanalytics> findById(SearchanalyticsId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredSearchanalytics::toDomain);
    }

    @Override
    public Optional<Searchanalytics> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredSearchanalytics::toDomain)
                .findFirst();
    }

    @Override
    public List<Searchanalytics> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredSearchanalytics row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredSearchanalytics::toDomain)
                .toList();
    }

    @Override
    public void delete(SearchanalyticsId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredSearchanalytics(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredSearchanalytics from(Searchanalytics aggregate) {
            return new StoredSearchanalytics(
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

        Searchanalytics toDomain() {
            return Searchanalytics.rehydrate(
                    SearchanalyticsId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    SearchanalyticsStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
