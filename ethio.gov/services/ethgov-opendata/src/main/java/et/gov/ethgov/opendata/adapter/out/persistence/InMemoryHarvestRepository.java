package et.gov.ethgov.opendata.adapter.out.persistence;

import et.gov.ethgov.opendata.domain.harvest.Harvest;
import et.gov.ethgov.opendata.domain.harvest.HarvestId;
import et.gov.ethgov.opendata.domain.harvest.HarvestRepository;
import et.gov.ethgov.opendata.domain.harvest.HarvestStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Harvest (replaceable with JPA). */
public final class InMemoryHarvestRepository implements HarvestRepository {
    private final Map<UUID, StoredHarvest> store = new ConcurrentHashMap<>();

    @Override
    public Harvest save(Harvest aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredHarvest.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Harvest> findById(HarvestId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredHarvest::toDomain);
    }

    @Override
    public Optional<Harvest> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredHarvest::toDomain)
                .findFirst();
    }

    @Override
    public List<Harvest> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredHarvest row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredHarvest::toDomain)
                .toList();
    }

    @Override
    public void delete(HarvestId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredHarvest(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredHarvest from(Harvest aggregate) {
            return new StoredHarvest(
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

        Harvest toDomain() {
            return Harvest.rehydrate(
                    HarvestId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    HarvestStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
