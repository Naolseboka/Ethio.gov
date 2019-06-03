package et.gov.ethgov.citizen.adapter.out.persistence;

import et.gov.ethgov.citizen.domain.emergencycontact.Emergencycontact;
import et.gov.ethgov.citizen.domain.emergencycontact.EmergencycontactId;
import et.gov.ethgov.citizen.domain.emergencycontact.EmergencycontactRepository;
import et.gov.ethgov.citizen.domain.emergencycontact.EmergencycontactStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Emergencycontact (replaceable with JPA). */
public final class InMemoryEmergencycontactRepository implements EmergencycontactRepository {
    private final Map<UUID, StoredEmergencycontact> store = new ConcurrentHashMap<>();

    @Override
    public Emergencycontact save(Emergencycontact aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredEmergencycontact.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Emergencycontact> findById(EmergencycontactId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredEmergencycontact::toDomain);
    }

    @Override
    public Optional<Emergencycontact> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredEmergencycontact::toDomain)
                .findFirst();
    }

    @Override
    public List<Emergencycontact> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredEmergencycontact row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredEmergencycontact::toDomain)
                .toList();
    }

    @Override
    public void delete(EmergencycontactId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredEmergencycontact(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredEmergencycontact from(Emergencycontact aggregate) {
            return new StoredEmergencycontact(
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

        Emergencycontact toDomain() {
            return Emergencycontact.rehydrate(
                    EmergencycontactId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    EmergencycontactStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
