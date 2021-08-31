package et.gov.ethgov.identity.adapter.out.persistence;

import et.gov.ethgov.identity.domain.delegation.Delegation;
import et.gov.ethgov.identity.domain.delegation.DelegationId;
import et.gov.ethgov.identity.domain.delegation.DelegationRepository;
import et.gov.ethgov.identity.domain.delegation.DelegationStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Delegation (replaceable with JPA). */
public final class InMemoryDelegationRepository implements DelegationRepository {
    private final Map<UUID, StoredDelegation> store = new ConcurrentHashMap<>();

    @Override
    public Delegation save(Delegation aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredDelegation.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Delegation> findById(DelegationId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredDelegation::toDomain);
    }

    @Override
    public Optional<Delegation> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredDelegation::toDomain)
                .findFirst();
    }

    @Override
    public List<Delegation> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredDelegation row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredDelegation::toDomain)
                .toList();
    }

    @Override
    public void delete(DelegationId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredDelegation(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredDelegation from(Delegation aggregate) {
            return new StoredDelegation(
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

        Delegation toDomain() {
            return Delegation.rehydrate(
                    DelegationId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    DelegationStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
