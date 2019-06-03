package et.gov.ethgov.citizen.adapter.out.persistence;

import et.gov.ethgov.citizen.domain.guardianship.Guardianship;
import et.gov.ethgov.citizen.domain.guardianship.GuardianshipId;
import et.gov.ethgov.citizen.domain.guardianship.GuardianshipRepository;
import et.gov.ethgov.citizen.domain.guardianship.GuardianshipStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Guardianship (replaceable with JPA). */
public final class InMemoryGuardianshipRepository implements GuardianshipRepository {
    private final Map<UUID, StoredGuardianship> store = new ConcurrentHashMap<>();

    @Override
    public Guardianship save(Guardianship aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredGuardianship.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Guardianship> findById(GuardianshipId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredGuardianship::toDomain);
    }

    @Override
    public Optional<Guardianship> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredGuardianship::toDomain)
                .findFirst();
    }

    @Override
    public List<Guardianship> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredGuardianship row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredGuardianship::toDomain)
                .toList();
    }

    @Override
    public void delete(GuardianshipId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredGuardianship(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredGuardianship from(Guardianship aggregate) {
            return new StoredGuardianship(
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

        Guardianship toDomain() {
            return Guardianship.rehydrate(
                    GuardianshipId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    GuardianshipStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
