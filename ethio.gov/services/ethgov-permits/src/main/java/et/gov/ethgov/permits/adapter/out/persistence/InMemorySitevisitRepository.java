package et.gov.ethgov.permits.adapter.out.persistence;

import et.gov.ethgov.permits.domain.sitevisit.Sitevisit;
import et.gov.ethgov.permits.domain.sitevisit.SitevisitId;
import et.gov.ethgov.permits.domain.sitevisit.SitevisitRepository;
import et.gov.ethgov.permits.domain.sitevisit.SitevisitStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Sitevisit (replaceable with JPA). */
public final class InMemorySitevisitRepository implements SitevisitRepository {
    private final Map<UUID, StoredSitevisit> store = new ConcurrentHashMap<>();

    @Override
    public Sitevisit save(Sitevisit aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredSitevisit.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Sitevisit> findById(SitevisitId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredSitevisit::toDomain);
    }

    @Override
    public Optional<Sitevisit> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredSitevisit::toDomain)
                .findFirst();
    }

    @Override
    public List<Sitevisit> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredSitevisit row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredSitevisit::toDomain)
                .toList();
    }

    @Override
    public void delete(SitevisitId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredSitevisit(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredSitevisit from(Sitevisit aggregate) {
            return new StoredSitevisit(
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

        Sitevisit toDomain() {
            return Sitevisit.rehydrate(
                    SitevisitId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    SitevisitStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
