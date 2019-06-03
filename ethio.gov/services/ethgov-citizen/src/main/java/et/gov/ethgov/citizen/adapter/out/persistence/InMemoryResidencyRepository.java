package et.gov.ethgov.citizen.adapter.out.persistence;

import et.gov.ethgov.citizen.domain.residency.Residency;
import et.gov.ethgov.citizen.domain.residency.ResidencyId;
import et.gov.ethgov.citizen.domain.residency.ResidencyRepository;
import et.gov.ethgov.citizen.domain.residency.ResidencyStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Residency (replaceable with JPA). */
public final class InMemoryResidencyRepository implements ResidencyRepository {
    private final Map<UUID, StoredResidency> store = new ConcurrentHashMap<>();

    @Override
    public Residency save(Residency aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredResidency.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Residency> findById(ResidencyId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredResidency::toDomain);
    }

    @Override
    public Optional<Residency> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredResidency::toDomain)
                .findFirst();
    }

    @Override
    public List<Residency> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredResidency row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredResidency::toDomain)
                .toList();
    }

    @Override
    public void delete(ResidencyId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredResidency(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredResidency from(Residency aggregate) {
            return new StoredResidency(
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

        Residency toDomain() {
            return Residency.rehydrate(
                    ResidencyId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    ResidencyStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
