package et.gov.ethgov.payments.adapter.out.persistence;

import et.gov.ethgov.payments.domain.allocation.Allocation;
import et.gov.ethgov.payments.domain.allocation.AllocationId;
import et.gov.ethgov.payments.domain.allocation.AllocationRepository;
import et.gov.ethgov.payments.domain.allocation.AllocationStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Allocation (replaceable with JPA). */
public final class InMemoryAllocationRepository implements AllocationRepository {
    private final Map<UUID, StoredAllocation> store = new ConcurrentHashMap<>();

    @Override
    public Allocation save(Allocation aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredAllocation.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Allocation> findById(AllocationId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredAllocation::toDomain);
    }

    @Override
    public Optional<Allocation> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredAllocation::toDomain)
                .findFirst();
    }

    @Override
    public List<Allocation> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredAllocation row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredAllocation::toDomain)
                .toList();
    }

    @Override
    public void delete(AllocationId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredAllocation(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredAllocation from(Allocation aggregate) {
            return new StoredAllocation(
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

        Allocation toDomain() {
            return Allocation.rehydrate(
                    AllocationId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    AllocationStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
