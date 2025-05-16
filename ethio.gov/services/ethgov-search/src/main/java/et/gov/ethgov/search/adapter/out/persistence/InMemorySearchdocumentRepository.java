package et.gov.ethgov.search.adapter.out.persistence;

import et.gov.ethgov.search.domain.searchdocument.Searchdocument;
import et.gov.ethgov.search.domain.searchdocument.SearchdocumentId;
import et.gov.ethgov.search.domain.searchdocument.SearchdocumentRepository;
import et.gov.ethgov.search.domain.searchdocument.SearchdocumentStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Searchdocument (replaceable with JPA). */
public final class InMemorySearchdocumentRepository implements SearchdocumentRepository {
    private final Map<UUID, StoredSearchdocument> store = new ConcurrentHashMap<>();

    @Override
    public Searchdocument save(Searchdocument aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredSearchdocument.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Searchdocument> findById(SearchdocumentId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredSearchdocument::toDomain);
    }

    @Override
    public Optional<Searchdocument> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredSearchdocument::toDomain)
                .findFirst();
    }

    @Override
    public List<Searchdocument> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredSearchdocument row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredSearchdocument::toDomain)
                .toList();
    }

    @Override
    public void delete(SearchdocumentId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredSearchdocument(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredSearchdocument from(Searchdocument aggregate) {
            return new StoredSearchdocument(
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

        Searchdocument toDomain() {
            return Searchdocument.rehydrate(
                    SearchdocumentId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    SearchdocumentStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
