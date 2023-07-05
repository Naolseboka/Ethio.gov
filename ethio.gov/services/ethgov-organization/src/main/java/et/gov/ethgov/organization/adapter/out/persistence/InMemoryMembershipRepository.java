package et.gov.ethgov.organization.adapter.out.persistence;

import et.gov.ethgov.organization.domain.membership.Membership;
import et.gov.ethgov.organization.domain.membership.MembershipId;
import et.gov.ethgov.organization.domain.membership.MembershipRepository;
import et.gov.ethgov.organization.domain.membership.MembershipStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Membership (replaceable with JPA). */
public final class InMemoryMembershipRepository implements MembershipRepository {
    private final Map<UUID, StoredMembership> store = new ConcurrentHashMap<>();

    @Override
    public Membership save(Membership aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredMembership.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Membership> findById(MembershipId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredMembership::toDomain);
    }

    @Override
    public Optional<Membership> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredMembership::toDomain)
                .findFirst();
    }

    @Override
    public List<Membership> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredMembership row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredMembership::toDomain)
                .toList();
    }

    @Override
    public void delete(MembershipId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredMembership(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredMembership from(Membership aggregate) {
            return new StoredMembership(
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

        Membership toDomain() {
            return Membership.rehydrate(
                    MembershipId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    MembershipStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
