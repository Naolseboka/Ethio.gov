package et.gov.ethgov.gis.adapter.out.persistence;

import et.gov.ethgov.gis.domain.layer.Layer;
import et.gov.ethgov.gis.domain.layer.LayerId;
import et.gov.ethgov.gis.domain.layer.LayerRepository;
import et.gov.ethgov.gis.domain.layer.LayerStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Layer (replaceable with JPA). */
public final class InMemoryLayerRepository implements LayerRepository {
    private final Map<UUID, StoredLayer> store = new ConcurrentHashMap<>();

    @Override
    public Layer save(Layer aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredLayer.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Layer> findById(LayerId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredLayer::toDomain);
    }

    @Override
    public Optional<Layer> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredLayer::toDomain)
                .findFirst();
    }

    @Override
    public List<Layer> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredLayer row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredLayer::toDomain)
                .toList();
    }

    @Override
    public void delete(LayerId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredLayer(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredLayer from(Layer aggregate) {
            return new StoredLayer(
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

        Layer toDomain() {
            return Layer.rehydrate(
                    LayerId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    LayerStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
