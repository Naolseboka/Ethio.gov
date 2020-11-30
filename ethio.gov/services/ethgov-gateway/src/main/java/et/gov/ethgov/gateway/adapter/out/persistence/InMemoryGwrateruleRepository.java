package et.gov.ethgov.gateway.adapter.out.persistence;

import et.gov.ethgov.gateway.domain.gwraterule.Gwraterule;
import et.gov.ethgov.gateway.domain.gwraterule.GwrateruleId;
import et.gov.ethgov.gateway.domain.gwraterule.GwrateruleRepository;
import et.gov.ethgov.gateway.domain.gwraterule.GwrateruleStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Gwraterule (replaceable with JPA). */
public final class InMemoryGwrateruleRepository implements GwrateruleRepository {
    private final Map<UUID, StoredGwraterule> store = new ConcurrentHashMap<>();

    @Override
    public Gwraterule save(Gwraterule aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredGwraterule.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Gwraterule> findById(GwrateruleId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredGwraterule::toDomain);
    }

    @Override
    public Optional<Gwraterule> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredGwraterule::toDomain)
                .findFirst();
    }

    @Override
    public List<Gwraterule> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredGwraterule row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredGwraterule::toDomain)
                .toList();
    }

    @Override
    public void delete(GwrateruleId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredGwraterule(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredGwraterule from(Gwraterule aggregate) {
            return new StoredGwraterule(
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

        Gwraterule toDomain() {
            return Gwraterule.rehydrate(
                    GwrateruleId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    GwrateruleStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
