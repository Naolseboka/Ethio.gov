package et.gov.ethgov.identity.adapter.out.persistence;

import et.gov.ethgov.identity.domain.lockout.Lockout;
import et.gov.ethgov.identity.domain.lockout.LockoutId;
import et.gov.ethgov.identity.domain.lockout.LockoutRepository;
import et.gov.ethgov.identity.domain.lockout.LockoutStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Lockout (replaceable with JPA). */
public final class InMemoryLockoutRepository implements LockoutRepository {
    private final Map<UUID, StoredLockout> store = new ConcurrentHashMap<>();

    @Override
    public Lockout save(Lockout aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredLockout.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Lockout> findById(LockoutId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredLockout::toDomain);
    }

    @Override
    public Optional<Lockout> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredLockout::toDomain)
                .findFirst();
    }

    @Override
    public List<Lockout> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredLockout row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredLockout::toDomain)
                .toList();
    }

    @Override
    public void delete(LockoutId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredLockout(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredLockout from(Lockout aggregate) {
            return new StoredLockout(
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

        Lockout toDomain() {
            return Lockout.rehydrate(
                    LockoutId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    LockoutStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
