package et.gov.ethgov.notification.adapter.out.persistence;

import et.gov.ethgov.notification.domain.delivery.Delivery;
import et.gov.ethgov.notification.domain.delivery.DeliveryId;
import et.gov.ethgov.notification.domain.delivery.DeliveryRepository;
import et.gov.ethgov.notification.domain.delivery.DeliveryStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Delivery (replaceable with JPA). */
public final class InMemoryDeliveryRepository implements DeliveryRepository {
    private final Map<UUID, StoredDelivery> store = new ConcurrentHashMap<>();

    @Override
    public Delivery save(Delivery aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredDelivery.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Delivery> findById(DeliveryId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredDelivery::toDomain);
    }

    @Override
    public Optional<Delivery> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredDelivery::toDomain)
                .findFirst();
    }

    @Override
    public List<Delivery> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredDelivery row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredDelivery::toDomain)
                .toList();
    }

    @Override
    public void delete(DeliveryId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredDelivery(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredDelivery from(Delivery aggregate) {
            return new StoredDelivery(
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

        Delivery toDomain() {
            return Delivery.rehydrate(
                    DeliveryId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    DeliveryStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
