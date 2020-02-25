package et.gov.ethgov.directory.adapter.out.persistence;

import et.gov.ethgov.directory.domain.facility.Facility;
import et.gov.ethgov.directory.domain.facility.FacilityId;
import et.gov.ethgov.directory.domain.facility.FacilityRepository;
import et.gov.ethgov.directory.domain.facility.FacilityStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Facility (replaceable with JPA). */
public final class InMemoryFacilityRepository implements FacilityRepository {
    private final Map<UUID, StoredFacility> store = new ConcurrentHashMap<>();

    @Override
    public Facility save(Facility aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredFacility.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Facility> findById(FacilityId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredFacility::toDomain);
    }

    @Override
    public Optional<Facility> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredFacility::toDomain)
                .findFirst();
    }

    @Override
    public List<Facility> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredFacility row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredFacility::toDomain)
                .toList();
    }

    @Override
    public void delete(FacilityId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredFacility(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredFacility from(Facility aggregate) {
            return new StoredFacility(
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

        Facility toDomain() {
            return Facility.rehydrate(
                    FacilityId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    FacilityStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
