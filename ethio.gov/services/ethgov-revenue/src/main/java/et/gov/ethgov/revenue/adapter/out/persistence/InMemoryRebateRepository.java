package et.gov.ethgov.revenue.adapter.out.persistence;

import et.gov.ethgov.revenue.domain.rebate.Rebate;
import et.gov.ethgov.revenue.domain.rebate.RebateId;
import et.gov.ethgov.revenue.domain.rebate.RebateRepository;
import et.gov.ethgov.revenue.domain.rebate.RebateStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Rebate (replaceable with JPA). */
public final class InMemoryRebateRepository implements RebateRepository {
    private final Map<UUID, StoredRebate> store = new ConcurrentHashMap<>();

    @Override
    public Rebate save(Rebate aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredRebate.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Rebate> findById(RebateId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredRebate::toDomain);
    }

    @Override
    public Optional<Rebate> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredRebate::toDomain)
                .findFirst();
    }

    @Override
    public List<Rebate> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredRebate row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredRebate::toDomain)
                .toList();
    }

    @Override
    public void delete(RebateId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredRebate(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredRebate from(Rebate aggregate) {
            return new StoredRebate(
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

        Rebate toDomain() {
            return Rebate.rehydrate(
                    RebateId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    RebateStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
