package et.gov.ethgov.citizen.adapter.out.persistence;

import et.gov.ethgov.citizen.domain.household.Household;
import et.gov.ethgov.citizen.domain.household.HouseholdId;
import et.gov.ethgov.citizen.domain.household.HouseholdRepository;
import et.gov.ethgov.citizen.domain.household.HouseholdStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Household (replaceable with JPA). */
public final class InMemoryHouseholdRepository implements HouseholdRepository {
    private final Map<UUID, StoredHousehold> store = new ConcurrentHashMap<>();

    @Override
    public Household save(Household aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredHousehold.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Household> findById(HouseholdId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredHousehold::toDomain);
    }

    @Override
    public Optional<Household> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredHousehold::toDomain)
                .findFirst();
    }

    @Override
    public List<Household> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredHousehold row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredHousehold::toDomain)
                .toList();
    }

    @Override
    public void delete(HouseholdId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredHousehold(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredHousehold from(Household aggregate) {
            return new StoredHousehold(
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

        Household toDomain() {
            return Household.rehydrate(
                    HouseholdId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    HouseholdStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
