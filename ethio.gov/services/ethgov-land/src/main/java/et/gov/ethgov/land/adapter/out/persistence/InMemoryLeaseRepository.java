package et.gov.ethgov.land.adapter.out.persistence;

import et.gov.ethgov.land.domain.lease.Lease;
import et.gov.ethgov.land.domain.lease.LeaseId;
import et.gov.ethgov.land.domain.lease.LeaseRepository;
import et.gov.ethgov.land.domain.lease.LeaseStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Lease (replaceable with JPA). */
public final class InMemoryLeaseRepository implements LeaseRepository {
    private final Map<UUID, StoredLease> store = new ConcurrentHashMap<>();

    @Override
    public Lease save(Lease aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredLease.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Lease> findById(LeaseId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredLease::toDomain);
    }

    @Override
    public Optional<Lease> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredLease::toDomain)
                .findFirst();
    }

    @Override
    public List<Lease> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredLease row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredLease::toDomain)
                .toList();
    }

    @Override
    public void delete(LeaseId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredLease(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredLease from(Lease aggregate) {
            return new StoredLease(
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

        Lease toDomain() {
            return Lease.rehydrate(
                    LeaseId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    LeaseStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
