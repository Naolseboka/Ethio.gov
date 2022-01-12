package et.gov.ethgov.land.adapter.out.persistence;

import et.gov.ethgov.land.domain.conflict.Conflict;
import et.gov.ethgov.land.domain.conflict.ConflictId;
import et.gov.ethgov.land.domain.conflict.ConflictRepository;
import et.gov.ethgov.land.domain.conflict.ConflictStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Conflict (replaceable with JPA). */
public final class InMemoryConflictRepository implements ConflictRepository {
    private final Map<UUID, StoredConflict> store = new ConcurrentHashMap<>();

    @Override
    public Conflict save(Conflict aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredConflict.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Conflict> findById(ConflictId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredConflict::toDomain);
    }

    @Override
    public Optional<Conflict> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredConflict::toDomain)
                .findFirst();
    }

    @Override
    public List<Conflict> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredConflict row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredConflict::toDomain)
                .toList();
    }

    @Override
    public void delete(ConflictId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredConflict(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredConflict from(Conflict aggregate) {
            return new StoredConflict(
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

        Conflict toDomain() {
            return Conflict.rehydrate(
                    ConflictId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    ConflictStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
