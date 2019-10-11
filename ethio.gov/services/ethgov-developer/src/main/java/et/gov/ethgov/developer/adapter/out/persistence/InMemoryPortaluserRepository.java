package et.gov.ethgov.developer.adapter.out.persistence;

import et.gov.ethgov.developer.domain.portaluser.Portaluser;
import et.gov.ethgov.developer.domain.portaluser.PortaluserId;
import et.gov.ethgov.developer.domain.portaluser.PortaluserRepository;
import et.gov.ethgov.developer.domain.portaluser.PortaluserStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Portaluser (replaceable with JPA). */
public final class InMemoryPortaluserRepository implements PortaluserRepository {
    private final Map<UUID, StoredPortaluser> store = new ConcurrentHashMap<>();

    @Override
    public Portaluser save(Portaluser aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredPortaluser.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Portaluser> findById(PortaluserId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredPortaluser::toDomain);
    }

    @Override
    public Optional<Portaluser> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredPortaluser::toDomain)
                .findFirst();
    }

    @Override
    public List<Portaluser> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredPortaluser row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredPortaluser::toDomain)
                .toList();
    }

    @Override
    public void delete(PortaluserId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredPortaluser(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredPortaluser from(Portaluser aggregate) {
            return new StoredPortaluser(
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

        Portaluser toDomain() {
            return Portaluser.rehydrate(
                    PortaluserId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    PortaluserStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
