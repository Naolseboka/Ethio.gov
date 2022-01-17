package et.gov.ethgov.land.adapter.out.persistence;

import et.gov.ethgov.land.domain.valuation.Valuation;
import et.gov.ethgov.land.domain.valuation.ValuationId;
import et.gov.ethgov.land.domain.valuation.ValuationRepository;
import et.gov.ethgov.land.domain.valuation.ValuationStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Valuation (replaceable with JPA). */
public final class InMemoryValuationRepository implements ValuationRepository {
    private final Map<UUID, StoredValuation> store = new ConcurrentHashMap<>();

    @Override
    public Valuation save(Valuation aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredValuation.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Valuation> findById(ValuationId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredValuation::toDomain);
    }

    @Override
    public Optional<Valuation> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredValuation::toDomain)
                .findFirst();
    }

    @Override
    public List<Valuation> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredValuation row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredValuation::toDomain)
                .toList();
    }

    @Override
    public void delete(ValuationId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredValuation(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredValuation from(Valuation aggregate) {
            return new StoredValuation(
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

        Valuation toDomain() {
            return Valuation.rehydrate(
                    ValuationId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    ValuationStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
