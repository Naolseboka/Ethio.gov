package et.gov.ethgov.permits.adapter.out.persistence;

import et.gov.ethgov.permits.domain.demolition.Demolition;
import et.gov.ethgov.permits.domain.demolition.DemolitionId;
import et.gov.ethgov.permits.domain.demolition.DemolitionRepository;
import et.gov.ethgov.permits.domain.demolition.DemolitionStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Demolition (replaceable with JPA). */
public final class InMemoryDemolitionRepository implements DemolitionRepository {
    private final Map<UUID, StoredDemolition> store = new ConcurrentHashMap<>();

    @Override
    public Demolition save(Demolition aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredDemolition.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Demolition> findById(DemolitionId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredDemolition::toDomain);
    }

    @Override
    public Optional<Demolition> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredDemolition::toDomain)
                .findFirst();
    }

    @Override
    public List<Demolition> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredDemolition row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredDemolition::toDomain)
                .toList();
    }

    @Override
    public void delete(DemolitionId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredDemolition(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredDemolition from(Demolition aggregate) {
            return new StoredDemolition(
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

        Demolition toDomain() {
            return Demolition.rehydrate(
                    DemolitionId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    DemolitionStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
