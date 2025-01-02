package et.gov.ethgov.revenue.adapter.out.persistence;

import et.gov.ethgov.revenue.domain.revenueappeal.Revenueappeal;
import et.gov.ethgov.revenue.domain.revenueappeal.RevenueappealId;
import et.gov.ethgov.revenue.domain.revenueappeal.RevenueappealRepository;
import et.gov.ethgov.revenue.domain.revenueappeal.RevenueappealStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Revenueappeal (replaceable with JPA). */
public final class InMemoryRevenueappealRepository implements RevenueappealRepository {
    private final Map<UUID, StoredRevenueappeal> store = new ConcurrentHashMap<>();

    @Override
    public Revenueappeal save(Revenueappeal aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredRevenueappeal.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Revenueappeal> findById(RevenueappealId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredRevenueappeal::toDomain);
    }

    @Override
    public Optional<Revenueappeal> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredRevenueappeal::toDomain)
                .findFirst();
    }

    @Override
    public List<Revenueappeal> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredRevenueappeal row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredRevenueappeal::toDomain)
                .toList();
    }

    @Override
    public void delete(RevenueappealId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredRevenueappeal(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredRevenueappeal from(Revenueappeal aggregate) {
            return new StoredRevenueappeal(
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

        Revenueappeal toDomain() {
            return Revenueappeal.rehydrate(
                    RevenueappealId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    RevenueappealStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
