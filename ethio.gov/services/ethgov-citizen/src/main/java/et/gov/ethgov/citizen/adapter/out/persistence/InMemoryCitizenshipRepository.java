package et.gov.ethgov.citizen.adapter.out.persistence;

import et.gov.ethgov.citizen.domain.citizenship.Citizenship;
import et.gov.ethgov.citizen.domain.citizenship.CitizenshipId;
import et.gov.ethgov.citizen.domain.citizenship.CitizenshipRepository;
import et.gov.ethgov.citizen.domain.citizenship.CitizenshipStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Citizenship (replaceable with JPA). */
public final class InMemoryCitizenshipRepository implements CitizenshipRepository {
    private final Map<UUID, StoredCitizenship> store = new ConcurrentHashMap<>();

    @Override
    public Citizenship save(Citizenship aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredCitizenship.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Citizenship> findById(CitizenshipId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredCitizenship::toDomain);
    }

    @Override
    public Optional<Citizenship> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredCitizenship::toDomain)
                .findFirst();
    }

    @Override
    public List<Citizenship> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredCitizenship row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredCitizenship::toDomain)
                .toList();
    }

    @Override
    public void delete(CitizenshipId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredCitizenship(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredCitizenship from(Citizenship aggregate) {
            return new StoredCitizenship(
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

        Citizenship toDomain() {
            return Citizenship.rehydrate(
                    CitizenshipId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    CitizenshipStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
