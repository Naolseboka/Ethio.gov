package et.gov.ethgov.search.adapter.out.persistence;

import et.gov.ethgov.search.domain.crawljob.Crawljob;
import et.gov.ethgov.search.domain.crawljob.CrawljobId;
import et.gov.ethgov.search.domain.crawljob.CrawljobRepository;
import et.gov.ethgov.search.domain.crawljob.CrawljobStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Crawljob (replaceable with JPA). */
public final class InMemoryCrawljobRepository implements CrawljobRepository {
    private final Map<UUID, StoredCrawljob> store = new ConcurrentHashMap<>();

    @Override
    public Crawljob save(Crawljob aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredCrawljob.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Crawljob> findById(CrawljobId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredCrawljob::toDomain);
    }

    @Override
    public Optional<Crawljob> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredCrawljob::toDomain)
                .findFirst();
    }

    @Override
    public List<Crawljob> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredCrawljob row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredCrawljob::toDomain)
                .toList();
    }

    @Override
    public void delete(CrawljobId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredCrawljob(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredCrawljob from(Crawljob aggregate) {
            return new StoredCrawljob(
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

        Crawljob toDomain() {
            return Crawljob.rehydrate(
                    CrawljobId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    CrawljobStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
