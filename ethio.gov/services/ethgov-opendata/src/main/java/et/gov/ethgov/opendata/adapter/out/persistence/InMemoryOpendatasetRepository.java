package et.gov.ethgov.opendata.adapter.out.persistence;

import et.gov.ethgov.opendata.domain.opendataset.Opendataset;
import et.gov.ethgov.opendata.domain.opendataset.OpendatasetId;
import et.gov.ethgov.opendata.domain.opendataset.OpendatasetRepository;
import et.gov.ethgov.opendata.domain.opendataset.OpendatasetStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Opendataset (replaceable with JPA). */
public final class InMemoryOpendatasetRepository implements OpendatasetRepository {
    private final Map<UUID, StoredOpendataset> store = new ConcurrentHashMap<>();

    @Override
    public Opendataset save(Opendataset aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredOpendataset.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Opendataset> findById(OpendatasetId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredOpendataset::toDomain);
    }

    @Override
    public Optional<Opendataset> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredOpendataset::toDomain)
                .findFirst();
    }

    @Override
    public List<Opendataset> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredOpendataset row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredOpendataset::toDomain)
                .toList();
    }

    @Override
    public void delete(OpendatasetId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredOpendataset(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredOpendataset from(Opendataset aggregate) {
            return new StoredOpendataset(
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

        Opendataset toDomain() {
            return Opendataset.rehydrate(
                    OpendatasetId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    OpendatasetStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
