package et.gov.ethgov.search.adapter.out.persistence;

import et.gov.ethgov.search.domain.facet.Facet;
import et.gov.ethgov.search.domain.facet.FacetId;
import et.gov.ethgov.search.domain.facet.FacetRepository;
import et.gov.ethgov.search.domain.facet.FacetStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Facet (replaceable with JPA). */
public final class InMemoryFacetRepository implements FacetRepository {
    private final Map<UUID, StoredFacet> store = new ConcurrentHashMap<>();

    @Override
    public Facet save(Facet aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredFacet.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Facet> findById(FacetId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredFacet::toDomain);
    }

    @Override
    public Optional<Facet> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredFacet::toDomain)
                .findFirst();
    }

    @Override
    public List<Facet> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredFacet row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredFacet::toDomain)
                .toList();
    }

    @Override
    public void delete(FacetId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredFacet(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredFacet from(Facet aggregate) {
            return new StoredFacet(
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

        Facet toDomain() {
            return Facet.rehydrate(
                    FacetId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    FacetStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
