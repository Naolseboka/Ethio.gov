package et.gov.ethgov.search.adapter.out.persistence;

import et.gov.ethgov.search.domain.synonym.Synonym;
import et.gov.ethgov.search.domain.synonym.SynonymId;
import et.gov.ethgov.search.domain.synonym.SynonymRepository;
import et.gov.ethgov.search.domain.synonym.SynonymStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Synonym (replaceable with JPA). */
public final class InMemorySynonymRepository implements SynonymRepository {
    private final Map<UUID, StoredSynonym> store = new ConcurrentHashMap<>();

    @Override
    public Synonym save(Synonym aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredSynonym.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Synonym> findById(SynonymId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredSynonym::toDomain);
    }

    @Override
    public Optional<Synonym> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredSynonym::toDomain)
                .findFirst();
    }

    @Override
    public List<Synonym> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredSynonym row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredSynonym::toDomain)
                .toList();
    }

    @Override
    public void delete(SynonymId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredSynonym(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredSynonym from(Synonym aggregate) {
            return new StoredSynonym(
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

        Synonym toDomain() {
            return Synonym.rehydrate(
                    SynonymId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    SynonymStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
