package et.gov.ethgov.opendata.adapter.out.persistence;

import et.gov.ethgov.opendata.domain.portalpage.Portalpage;
import et.gov.ethgov.opendata.domain.portalpage.PortalpageId;
import et.gov.ethgov.opendata.domain.portalpage.PortalpageRepository;
import et.gov.ethgov.opendata.domain.portalpage.PortalpageStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Portalpage (replaceable with JPA). */
public final class InMemoryPortalpageRepository implements PortalpageRepository {
    private final Map<UUID, StoredPortalpage> store = new ConcurrentHashMap<>();

    @Override
    public Portalpage save(Portalpage aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredPortalpage.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Portalpage> findById(PortalpageId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredPortalpage::toDomain);
    }

    @Override
    public Optional<Portalpage> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredPortalpage::toDomain)
                .findFirst();
    }

    @Override
    public List<Portalpage> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredPortalpage row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredPortalpage::toDomain)
                .toList();
    }

    @Override
    public void delete(PortalpageId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredPortalpage(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredPortalpage from(Portalpage aggregate) {
            return new StoredPortalpage(
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

        Portalpage toDomain() {
            return Portalpage.rehydrate(
                    PortalpageId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    PortalpageStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
