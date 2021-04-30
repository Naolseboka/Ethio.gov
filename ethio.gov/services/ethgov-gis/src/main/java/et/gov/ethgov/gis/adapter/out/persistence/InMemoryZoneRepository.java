package et.gov.ethgov.gis.adapter.out.persistence;

import et.gov.ethgov.gis.domain.zone.Zone;
import et.gov.ethgov.gis.domain.zone.ZoneId;
import et.gov.ethgov.gis.domain.zone.ZoneRepository;
import et.gov.ethgov.gis.domain.zone.ZoneStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Zone (replaceable with JPA). */
public final class InMemoryZoneRepository implements ZoneRepository {
    private final Map<UUID, StoredZone> store = new ConcurrentHashMap<>();

    @Override
    public Zone save(Zone aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredZone.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Zone> findById(ZoneId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredZone::toDomain);
    }

    @Override
    public Optional<Zone> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredZone::toDomain)
                .findFirst();
    }

    @Override
    public List<Zone> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredZone row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredZone::toDomain)
                .toList();
    }

    @Override
    public void delete(ZoneId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredZone(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredZone from(Zone aggregate) {
            return new StoredZone(
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

        Zone toDomain() {
            return Zone.rehydrate(
                    ZoneId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    ZoneStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
