package et.gov.ethgov.organization.adapter.out.persistence;

import et.gov.ethgov.organization.domain.orgaddress.Orgaddress;
import et.gov.ethgov.organization.domain.orgaddress.OrgaddressId;
import et.gov.ethgov.organization.domain.orgaddress.OrgaddressRepository;
import et.gov.ethgov.organization.domain.orgaddress.OrgaddressStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Orgaddress (replaceable with JPA). */
public final class InMemoryOrgaddressRepository implements OrgaddressRepository {
    private final Map<UUID, StoredOrgaddress> store = new ConcurrentHashMap<>();

    @Override
    public Orgaddress save(Orgaddress aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredOrgaddress.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Orgaddress> findById(OrgaddressId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredOrgaddress::toDomain);
    }

    @Override
    public Optional<Orgaddress> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredOrgaddress::toDomain)
                .findFirst();
    }

    @Override
    public List<Orgaddress> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredOrgaddress row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredOrgaddress::toDomain)
                .toList();
    }

    @Override
    public void delete(OrgaddressId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredOrgaddress(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredOrgaddress from(Orgaddress aggregate) {
            return new StoredOrgaddress(
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

        Orgaddress toDomain() {
            return Orgaddress.rehydrate(
                    OrgaddressId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    OrgaddressStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
