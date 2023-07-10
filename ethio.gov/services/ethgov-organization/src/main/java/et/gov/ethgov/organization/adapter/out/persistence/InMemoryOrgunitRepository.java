package et.gov.ethgov.organization.adapter.out.persistence;

import et.gov.ethgov.organization.domain.orgunit.Orgunit;
import et.gov.ethgov.organization.domain.orgunit.OrgunitId;
import et.gov.ethgov.organization.domain.orgunit.OrgunitRepository;
import et.gov.ethgov.organization.domain.orgunit.OrgunitStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Orgunit (replaceable with JPA). */
public final class InMemoryOrgunitRepository implements OrgunitRepository {
    private final Map<UUID, StoredOrgunit> store = new ConcurrentHashMap<>();

    @Override
    public Orgunit save(Orgunit aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredOrgunit.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Orgunit> findById(OrgunitId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredOrgunit::toDomain);
    }

    @Override
    public Optional<Orgunit> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredOrgunit::toDomain)
                .findFirst();
    }

    @Override
    public List<Orgunit> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredOrgunit row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredOrgunit::toDomain)
                .toList();
    }

    @Override
    public void delete(OrgunitId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredOrgunit(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredOrgunit from(Orgunit aggregate) {
            return new StoredOrgunit(
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

        Orgunit toDomain() {
            return Orgunit.rehydrate(
                    OrgunitId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    OrgunitStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
