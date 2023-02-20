package et.gov.ethgov.opendata.adapter.out.persistence;

import et.gov.ethgov.opendata.domain.dataupdate.Dataupdate;
import et.gov.ethgov.opendata.domain.dataupdate.DataupdateId;
import et.gov.ethgov.opendata.domain.dataupdate.DataupdateRepository;
import et.gov.ethgov.opendata.domain.dataupdate.DataupdateStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Dataupdate (replaceable with JPA). */
public final class InMemoryDataupdateRepository implements DataupdateRepository {
    private final Map<UUID, StoredDataupdate> store = new ConcurrentHashMap<>();

    @Override
    public Dataupdate save(Dataupdate aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredDataupdate.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Dataupdate> findById(DataupdateId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredDataupdate::toDomain);
    }

    @Override
    public Optional<Dataupdate> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredDataupdate::toDomain)
                .findFirst();
    }

    @Override
    public List<Dataupdate> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredDataupdate row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredDataupdate::toDomain)
                .toList();
    }

    @Override
    public void delete(DataupdateId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredDataupdate(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredDataupdate from(Dataupdate aggregate) {
            return new StoredDataupdate(
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

        Dataupdate toDomain() {
            return Dataupdate.rehydrate(
                    DataupdateId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    DataupdateStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
