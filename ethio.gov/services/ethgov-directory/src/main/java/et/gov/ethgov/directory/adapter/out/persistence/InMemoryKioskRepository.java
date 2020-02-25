package et.gov.ethgov.directory.adapter.out.persistence;

import et.gov.ethgov.directory.domain.kiosk.Kiosk;
import et.gov.ethgov.directory.domain.kiosk.KioskId;
import et.gov.ethgov.directory.domain.kiosk.KioskRepository;
import et.gov.ethgov.directory.domain.kiosk.KioskStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Kiosk (replaceable with JPA). */
public final class InMemoryKioskRepository implements KioskRepository {
    private final Map<UUID, StoredKiosk> store = new ConcurrentHashMap<>();

    @Override
    public Kiosk save(Kiosk aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredKiosk.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Kiosk> findById(KioskId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredKiosk::toDomain);
    }

    @Override
    public Optional<Kiosk> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredKiosk::toDomain)
                .findFirst();
    }

    @Override
    public List<Kiosk> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredKiosk row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredKiosk::toDomain)
                .toList();
    }

    @Override
    public void delete(KioskId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredKiosk(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredKiosk from(Kiosk aggregate) {
            return new StoredKiosk(
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

        Kiosk toDomain() {
            return Kiosk.rehydrate(
                    KioskId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    KioskStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
