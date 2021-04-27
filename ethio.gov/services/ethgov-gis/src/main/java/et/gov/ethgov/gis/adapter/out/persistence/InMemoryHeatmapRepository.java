package et.gov.ethgov.gis.adapter.out.persistence;

import et.gov.ethgov.gis.domain.heatmap.Heatmap;
import et.gov.ethgov.gis.domain.heatmap.HeatmapId;
import et.gov.ethgov.gis.domain.heatmap.HeatmapRepository;
import et.gov.ethgov.gis.domain.heatmap.HeatmapStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Heatmap (replaceable with JPA). */
public final class InMemoryHeatmapRepository implements HeatmapRepository {
    private final Map<UUID, StoredHeatmap> store = new ConcurrentHashMap<>();

    @Override
    public Heatmap save(Heatmap aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredHeatmap.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Heatmap> findById(HeatmapId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredHeatmap::toDomain);
    }

    @Override
    public Optional<Heatmap> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredHeatmap::toDomain)
                .findFirst();
    }

    @Override
    public List<Heatmap> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredHeatmap row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredHeatmap::toDomain)
                .toList();
    }

    @Override
    public void delete(HeatmapId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredHeatmap(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredHeatmap from(Heatmap aggregate) {
            return new StoredHeatmap(
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

        Heatmap toDomain() {
            return Heatmap.rehydrate(
                    HeatmapId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    HeatmapStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
