package et.gov.ethgov.gis.adapter.out.persistence;

import et.gov.ethgov.gis.domain.geocode.Geocode;
import et.gov.ethgov.gis.domain.geocode.GeocodeId;
import et.gov.ethgov.gis.domain.geocode.GeocodeRepository;
import et.gov.ethgov.gis.domain.geocode.GeocodeStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Geocode (replaceable with JPA). */
public final class InMemoryGeocodeRepository implements GeocodeRepository {
    private final Map<UUID, StoredGeocode> store = new ConcurrentHashMap<>();

    @Override
    public Geocode save(Geocode aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredGeocode.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Geocode> findById(GeocodeId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredGeocode::toDomain);
    }

    @Override
    public Optional<Geocode> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredGeocode::toDomain)
                .findFirst();
    }

    @Override
    public List<Geocode> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredGeocode row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredGeocode::toDomain)
                .toList();
    }

    @Override
    public void delete(GeocodeId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredGeocode(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredGeocode from(Geocode aggregate) {
            return new StoredGeocode(
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

        Geocode toDomain() {
            return Geocode.rehydrate(
                    GeocodeId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    GeocodeStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
