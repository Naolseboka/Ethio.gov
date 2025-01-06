package et.gov.ethgov.revenue.adapter.out.persistence;

import et.gov.ethgov.revenue.domain.writeoff.Writeoff;
import et.gov.ethgov.revenue.domain.writeoff.WriteoffId;
import et.gov.ethgov.revenue.domain.writeoff.WriteoffRepository;
import et.gov.ethgov.revenue.domain.writeoff.WriteoffStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Writeoff (replaceable with JPA). */
public final class InMemoryWriteoffRepository implements WriteoffRepository {
    private final Map<UUID, StoredWriteoff> store = new ConcurrentHashMap<>();

    @Override
    public Writeoff save(Writeoff aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredWriteoff.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Writeoff> findById(WriteoffId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredWriteoff::toDomain);
    }

    @Override
    public Optional<Writeoff> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredWriteoff::toDomain)
                .findFirst();
    }

    @Override
    public List<Writeoff> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredWriteoff row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredWriteoff::toDomain)
                .toList();
    }

    @Override
    public void delete(WriteoffId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredWriteoff(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredWriteoff from(Writeoff aggregate) {
            return new StoredWriteoff(
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

        Writeoff toDomain() {
            return Writeoff.rehydrate(
                    WriteoffId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    WriteoffStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
