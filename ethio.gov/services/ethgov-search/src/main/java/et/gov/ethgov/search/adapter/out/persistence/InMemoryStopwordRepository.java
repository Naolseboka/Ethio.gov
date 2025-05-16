package et.gov.ethgov.search.adapter.out.persistence;

import et.gov.ethgov.search.domain.stopword.Stopword;
import et.gov.ethgov.search.domain.stopword.StopwordId;
import et.gov.ethgov.search.domain.stopword.StopwordRepository;
import et.gov.ethgov.search.domain.stopword.StopwordStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Stopword (replaceable with JPA). */
public final class InMemoryStopwordRepository implements StopwordRepository {
    private final Map<UUID, StoredStopword> store = new ConcurrentHashMap<>();

    @Override
    public Stopword save(Stopword aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredStopword.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Stopword> findById(StopwordId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredStopword::toDomain);
    }

    @Override
    public Optional<Stopword> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredStopword::toDomain)
                .findFirst();
    }

    @Override
    public List<Stopword> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredStopword row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredStopword::toDomain)
                .toList();
    }

    @Override
    public void delete(StopwordId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredStopword(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredStopword from(Stopword aggregate) {
            return new StoredStopword(
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

        Stopword toDomain() {
            return Stopword.rehydrate(
                    StopwordId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    StopwordStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
