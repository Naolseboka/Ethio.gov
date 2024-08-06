package et.gov.ethgov.reporting.adapter.out.persistence;

import et.gov.ethgov.reporting.domain.kpi.Kpi;
import et.gov.ethgov.reporting.domain.kpi.KpiId;
import et.gov.ethgov.reporting.domain.kpi.KpiRepository;
import et.gov.ethgov.reporting.domain.kpi.KpiStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Kpi (replaceable with JPA). */
public final class InMemoryKpiRepository implements KpiRepository {
    private final Map<UUID, StoredKpi> store = new ConcurrentHashMap<>();

    @Override
    public Kpi save(Kpi aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredKpi.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Kpi> findById(KpiId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredKpi::toDomain);
    }

    @Override
    public Optional<Kpi> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredKpi::toDomain)
                .findFirst();
    }

    @Override
    public List<Kpi> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredKpi row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredKpi::toDomain)
                .toList();
    }

    @Override
    public void delete(KpiId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredKpi(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredKpi from(Kpi aggregate) {
            return new StoredKpi(
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

        Kpi toDomain() {
            return Kpi.rehydrate(
                    KpiId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    KpiStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
