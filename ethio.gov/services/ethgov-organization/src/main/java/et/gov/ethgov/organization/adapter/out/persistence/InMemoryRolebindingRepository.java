package et.gov.ethgov.organization.adapter.out.persistence;

import et.gov.ethgov.organization.domain.rolebinding.Rolebinding;
import et.gov.ethgov.organization.domain.rolebinding.RolebindingId;
import et.gov.ethgov.organization.domain.rolebinding.RolebindingRepository;
import et.gov.ethgov.organization.domain.rolebinding.RolebindingStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Rolebinding (replaceable with JPA). */
public final class InMemoryRolebindingRepository implements RolebindingRepository {
    private final Map<UUID, StoredRolebinding> store = new ConcurrentHashMap<>();

    @Override
    public Rolebinding save(Rolebinding aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredRolebinding.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Rolebinding> findById(RolebindingId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredRolebinding::toDomain);
    }

    @Override
    public Optional<Rolebinding> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredRolebinding::toDomain)
                .findFirst();
    }

    @Override
    public List<Rolebinding> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredRolebinding row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredRolebinding::toDomain)
                .toList();
    }

    @Override
    public void delete(RolebindingId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredRolebinding(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredRolebinding from(Rolebinding aggregate) {
            return new StoredRolebinding(
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

        Rolebinding toDomain() {
            return Rolebinding.rehydrate(
                    RolebindingId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    RolebindingStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
