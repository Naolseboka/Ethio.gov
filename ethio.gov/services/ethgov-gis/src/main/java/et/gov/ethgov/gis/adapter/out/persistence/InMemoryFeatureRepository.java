package et.gov.ethgov.gis.adapter.out.persistence;

import et.gov.ethgov.gis.domain.feature.Feature;
import et.gov.ethgov.gis.domain.feature.FeatureId;
import et.gov.ethgov.gis.domain.feature.FeatureRepository;
import et.gov.ethgov.gis.domain.feature.FeatureStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Feature (replaceable with JPA). */
public final class InMemoryFeatureRepository implements FeatureRepository {
    private final Map<UUID, StoredFeature> store = new ConcurrentHashMap<>();

    @Override
    public Feature save(Feature aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredFeature.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Feature> findById(FeatureId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredFeature::toDomain);
    }

    @Override
    public Optional<Feature> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredFeature::toDomain)
                .findFirst();
    }

    @Override
    public List<Feature> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredFeature row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredFeature::toDomain)
                .toList();
    }

    @Override
    public void delete(FeatureId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredFeature(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredFeature from(Feature aggregate) {
            return new StoredFeature(
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

        Feature toDomain() {
            return Feature.rehydrate(
                    FeatureId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    FeatureStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
