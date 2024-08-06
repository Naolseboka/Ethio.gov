package et.gov.ethgov.reporting.adapter.out.persistence;

import et.gov.ethgov.reporting.domain.dataset.Dataset;
import et.gov.ethgov.reporting.domain.dataset.DatasetId;
import et.gov.ethgov.reporting.domain.dataset.DatasetRepository;
import et.gov.ethgov.reporting.domain.dataset.DatasetStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Dataset (replaceable with JPA). */
public final class InMemoryDatasetRepository implements DatasetRepository {
    private final Map<UUID, StoredDataset> store = new ConcurrentHashMap<>();

    @Override
    public Dataset save(Dataset aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredDataset.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Dataset> findById(DatasetId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredDataset::toDomain);
    }

    @Override
    public Optional<Dataset> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredDataset::toDomain)
                .findFirst();
    }

    @Override
    public List<Dataset> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredDataset row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredDataset::toDomain)
                .toList();
    }

    @Override
    public void delete(DatasetId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredDataset(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredDataset from(Dataset aggregate) {
            return new StoredDataset(
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

        Dataset toDomain() {
            return Dataset.rehydrate(
                    DatasetId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    DatasetStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
