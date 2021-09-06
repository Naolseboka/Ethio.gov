package et.gov.ethgov.identity.adapter.out.persistence;

import et.gov.ethgov.identity.domain.recovery.Recovery;
import et.gov.ethgov.identity.domain.recovery.RecoveryId;
import et.gov.ethgov.identity.domain.recovery.RecoveryRepository;
import et.gov.ethgov.identity.domain.recovery.RecoveryStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Recovery (replaceable with JPA). */
public final class InMemoryRecoveryRepository implements RecoveryRepository {
    private final Map<UUID, StoredRecovery> store = new ConcurrentHashMap<>();

    @Override
    public Recovery save(Recovery aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredRecovery.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Recovery> findById(RecoveryId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredRecovery::toDomain);
    }

    @Override
    public Optional<Recovery> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredRecovery::toDomain)
                .findFirst();
    }

    @Override
    public List<Recovery> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredRecovery row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredRecovery::toDomain)
                .toList();
    }

    @Override
    public void delete(RecoveryId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredRecovery(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredRecovery from(Recovery aggregate) {
            return new StoredRecovery(
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

        Recovery toDomain() {
            return Recovery.rehydrate(
                    RecoveryId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    RecoveryStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
