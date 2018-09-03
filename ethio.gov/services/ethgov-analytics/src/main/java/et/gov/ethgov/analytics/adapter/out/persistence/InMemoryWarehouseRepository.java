package et.gov.ethgov.analytics.adapter.out.persistence;

import et.gov.ethgov.analytics.domain.warehouse.Warehouse;
import et.gov.ethgov.analytics.domain.warehouse.WarehouseId;
import et.gov.ethgov.analytics.domain.warehouse.WarehouseRepository;
import et.gov.ethgov.analytics.domain.warehouse.WarehouseStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Warehouse (replaceable with JPA). */
public final class InMemoryWarehouseRepository implements WarehouseRepository {
    private final Map<UUID, StoredWarehouse> store = new ConcurrentHashMap<>();

    @Override
    public Warehouse save(Warehouse aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredWarehouse.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Warehouse> findById(WarehouseId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredWarehouse::toDomain);
    }

    @Override
    public Optional<Warehouse> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredWarehouse::toDomain)
                .findFirst();
    }

    @Override
    public List<Warehouse> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredWarehouse row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredWarehouse::toDomain)
                .toList();
    }

    @Override
    public void delete(WarehouseId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredWarehouse(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredWarehouse from(Warehouse aggregate) {
            return new StoredWarehouse(
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

        Warehouse toDomain() {
            return Warehouse.rehydrate(
                    WarehouseId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    WarehouseStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
