package et.gov.ethgov.organization.adapter.out.persistence;

import et.gov.ethgov.organization.domain.orgcontact.Orgcontact;
import et.gov.ethgov.organization.domain.orgcontact.OrgcontactId;
import et.gov.ethgov.organization.domain.orgcontact.OrgcontactRepository;
import et.gov.ethgov.organization.domain.orgcontact.OrgcontactStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Orgcontact (replaceable with JPA). */
public final class InMemoryOrgcontactRepository implements OrgcontactRepository {
    private final Map<UUID, StoredOrgcontact> store = new ConcurrentHashMap<>();

    @Override
    public Orgcontact save(Orgcontact aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredOrgcontact.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Orgcontact> findById(OrgcontactId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredOrgcontact::toDomain);
    }

    @Override
    public Optional<Orgcontact> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredOrgcontact::toDomain)
                .findFirst();
    }

    @Override
    public List<Orgcontact> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredOrgcontact row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredOrgcontact::toDomain)
                .toList();
    }

    @Override
    public void delete(OrgcontactId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredOrgcontact(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredOrgcontact from(Orgcontact aggregate) {
            return new StoredOrgcontact(
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

        Orgcontact toDomain() {
            return Orgcontact.rehydrate(
                    OrgcontactId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    OrgcontactStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
