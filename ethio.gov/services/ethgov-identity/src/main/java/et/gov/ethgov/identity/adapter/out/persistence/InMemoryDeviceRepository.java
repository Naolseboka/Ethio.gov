package et.gov.ethgov.identity.adapter.out.persistence;

import et.gov.ethgov.identity.domain.device.Device;
import et.gov.ethgov.identity.domain.device.DeviceId;
import et.gov.ethgov.identity.domain.device.DeviceRepository;
import et.gov.ethgov.identity.domain.device.DeviceStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Device (replaceable with JPA). */
public final class InMemoryDeviceRepository implements DeviceRepository {
    private final Map<UUID, StoredDevice> store = new ConcurrentHashMap<>();

    @Override
    public Device save(Device aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredDevice.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Device> findById(DeviceId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredDevice::toDomain);
    }

    @Override
    public Optional<Device> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredDevice::toDomain)
                .findFirst();
    }

    @Override
    public List<Device> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredDevice row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredDevice::toDomain)
                .toList();
    }

    @Override
    public void delete(DeviceId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredDevice(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredDevice from(Device aggregate) {
            return new StoredDevice(
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

        Device toDomain() {
            return Device.rehydrate(
                    DeviceId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    DeviceStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
