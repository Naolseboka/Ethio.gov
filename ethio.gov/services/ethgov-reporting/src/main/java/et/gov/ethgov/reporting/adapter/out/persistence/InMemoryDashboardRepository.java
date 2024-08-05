package et.gov.ethgov.reporting.adapter.out.persistence;

import et.gov.ethgov.reporting.domain.dashboard.Dashboard;
import et.gov.ethgov.reporting.domain.dashboard.DashboardId;
import et.gov.ethgov.reporting.domain.dashboard.DashboardRepository;
import et.gov.ethgov.reporting.domain.dashboard.DashboardStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Dashboard (replaceable with JPA). */
public final class InMemoryDashboardRepository implements DashboardRepository {
    private final Map<UUID, StoredDashboard> store = new ConcurrentHashMap<>();

    @Override
    public Dashboard save(Dashboard aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredDashboard.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Dashboard> findById(DashboardId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredDashboard::toDomain);
    }

    @Override
    public Optional<Dashboard> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredDashboard::toDomain)
                .findFirst();
    }

    @Override
    public List<Dashboard> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredDashboard row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredDashboard::toDomain)
                .toList();
    }

    @Override
    public void delete(DashboardId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredDashboard(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredDashboard from(Dashboard aggregate) {
            return new StoredDashboard(
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

        Dashboard toDomain() {
            return Dashboard.rehydrate(
                    DashboardId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    DashboardStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
