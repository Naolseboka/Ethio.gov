package et.gov.ethgov.citizen.adapter.out.persistence;

import et.gov.ethgov.citizen.domain.disabilitystatus.Disabilitystatus;
import et.gov.ethgov.citizen.domain.disabilitystatus.DisabilitystatusId;
import et.gov.ethgov.citizen.domain.disabilitystatus.DisabilitystatusRepository;
import et.gov.ethgov.citizen.domain.disabilitystatus.DisabilitystatusStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Disabilitystatus (replaceable with JPA). */
public final class InMemoryDisabilitystatusRepository implements DisabilitystatusRepository {
    private final Map<UUID, StoredDisabilitystatus> store = new ConcurrentHashMap<>();

    @Override
    public Disabilitystatus save(Disabilitystatus aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredDisabilitystatus.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Disabilitystatus> findById(DisabilitystatusId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredDisabilitystatus::toDomain);
    }

    @Override
    public Optional<Disabilitystatus> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredDisabilitystatus::toDomain)
                .findFirst();
    }

    @Override
    public List<Disabilitystatus> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredDisabilitystatus row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredDisabilitystatus::toDomain)
                .toList();
    }

    @Override
    public void delete(DisabilitystatusId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredDisabilitystatus(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredDisabilitystatus from(Disabilitystatus aggregate) {
            return new StoredDisabilitystatus(
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

        Disabilitystatus toDomain() {
            return Disabilitystatus.rehydrate(
                    DisabilitystatusId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    DisabilitystatusStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
