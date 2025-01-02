package et.gov.ethgov.revenue.adapter.out.persistence;

import et.gov.ethgov.revenue.domain.fiscalyear.Fiscalyear;
import et.gov.ethgov.revenue.domain.fiscalyear.FiscalyearId;
import et.gov.ethgov.revenue.domain.fiscalyear.FiscalyearRepository;
import et.gov.ethgov.revenue.domain.fiscalyear.FiscalyearStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Fiscalyear (replaceable with JPA). */
public final class InMemoryFiscalyearRepository implements FiscalyearRepository {
    private final Map<UUID, StoredFiscalyear> store = new ConcurrentHashMap<>();

    @Override
    public Fiscalyear save(Fiscalyear aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredFiscalyear.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Fiscalyear> findById(FiscalyearId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredFiscalyear::toDomain);
    }

    @Override
    public Optional<Fiscalyear> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredFiscalyear::toDomain)
                .findFirst();
    }

    @Override
    public List<Fiscalyear> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredFiscalyear row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredFiscalyear::toDomain)
                .toList();
    }

    @Override
    public void delete(FiscalyearId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredFiscalyear(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredFiscalyear from(Fiscalyear aggregate) {
            return new StoredFiscalyear(
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

        Fiscalyear toDomain() {
            return Fiscalyear.rehydrate(
                    FiscalyearId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    FiscalyearStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
