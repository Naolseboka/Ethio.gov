package et.gov.ethgov.land.adapter.out.persistence;

import et.gov.ethgov.land.domain.parcel.Parcel;
import et.gov.ethgov.land.domain.parcel.ParcelId;
import et.gov.ethgov.land.domain.parcel.ParcelRepository;
import et.gov.ethgov.land.domain.parcel.ParcelStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Parcel (replaceable with JPA). */
public final class InMemoryParcelRepository implements ParcelRepository {
    private final Map<UUID, StoredParcel> store = new ConcurrentHashMap<>();

    @Override
    public Parcel save(Parcel aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredParcel.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Parcel> findById(ParcelId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredParcel::toDomain);
    }

    @Override
    public Optional<Parcel> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredParcel::toDomain)
                .findFirst();
    }

    @Override
    public List<Parcel> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredParcel row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredParcel::toDomain)
                .toList();
    }

    @Override
    public void delete(ParcelId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredParcel(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredParcel from(Parcel aggregate) {
            return new StoredParcel(
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

        Parcel toDomain() {
            return Parcel.rehydrate(
                    ParcelId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    ParcelStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
