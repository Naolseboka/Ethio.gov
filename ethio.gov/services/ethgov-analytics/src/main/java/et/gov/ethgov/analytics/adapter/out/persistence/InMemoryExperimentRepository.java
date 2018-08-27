package et.gov.ethgov.analytics.adapter.out.persistence;

import et.gov.ethgov.analytics.domain.experiment.Experiment;
import et.gov.ethgov.analytics.domain.experiment.ExperimentId;
import et.gov.ethgov.analytics.domain.experiment.ExperimentRepository;
import et.gov.ethgov.analytics.domain.experiment.ExperimentStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Experiment (replaceable with JPA). */
public final class InMemoryExperimentRepository implements ExperimentRepository {
    private final Map<UUID, StoredExperiment> store = new ConcurrentHashMap<>();

    @Override
    public Experiment save(Experiment aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredExperiment.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Experiment> findById(ExperimentId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredExperiment::toDomain);
    }

    @Override
    public Optional<Experiment> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredExperiment::toDomain)
                .findFirst();
    }

    @Override
    public List<Experiment> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredExperiment row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredExperiment::toDomain)
                .toList();
    }

    @Override
    public void delete(ExperimentId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredExperiment(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredExperiment from(Experiment aggregate) {
            return new StoredExperiment(
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

        Experiment toDomain() {
            return Experiment.rehydrate(
                    ExperimentId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    ExperimentStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
