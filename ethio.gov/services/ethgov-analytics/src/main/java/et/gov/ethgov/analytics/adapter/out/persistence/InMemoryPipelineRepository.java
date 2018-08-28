package et.gov.ethgov.analytics.adapter.out.persistence;

import et.gov.ethgov.analytics.domain.pipeline.Pipeline;
import et.gov.ethgov.analytics.domain.pipeline.PipelineId;
import et.gov.ethgov.analytics.domain.pipeline.PipelineRepository;
import et.gov.ethgov.analytics.domain.pipeline.PipelineStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Pipeline (replaceable with JPA). */
public final class InMemoryPipelineRepository implements PipelineRepository {
    private final Map<UUID, StoredPipeline> store = new ConcurrentHashMap<>();

    @Override
    public Pipeline save(Pipeline aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredPipeline.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Pipeline> findById(PipelineId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredPipeline::toDomain);
    }

    @Override
    public Optional<Pipeline> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredPipeline::toDomain)
                .findFirst();
    }

    @Override
    public List<Pipeline> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredPipeline row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredPipeline::toDomain)
                .toList();
    }

    @Override
    public void delete(PipelineId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredPipeline(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredPipeline from(Pipeline aggregate) {
            return new StoredPipeline(
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

        Pipeline toDomain() {
            return Pipeline.rehydrate(
                    PipelineId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    PipelineStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
