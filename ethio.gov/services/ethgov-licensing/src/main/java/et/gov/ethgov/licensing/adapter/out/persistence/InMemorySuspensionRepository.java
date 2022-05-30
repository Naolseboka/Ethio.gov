package et.gov.ethgov.licensing.adapter.out.persistence;

import et.gov.ethgov.licensing.domain.suspension.Suspension;
import et.gov.ethgov.licensing.domain.suspension.SuspensionId;
import et.gov.ethgov.licensing.domain.suspension.SuspensionRepository;
import et.gov.ethgov.licensing.domain.suspension.SuspensionStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Suspension (replaceable with JPA). */
public final class InMemorySuspensionRepository implements SuspensionRepository {
    private final Map<UUID, StoredSuspension> store = new ConcurrentHashMap<>();

    @Override
    public Suspension save(Suspension aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredSuspension.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Suspension> findById(SuspensionId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredSuspension::toDomain);
    }

    @Override
    public Optional<Suspension> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredSuspension::toDomain)
                .findFirst();
    }

    @Override
    public List<Suspension> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredSuspension row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredSuspension::toDomain)
                .toList();
    }

    @Override
    public void delete(SuspensionId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredSuspension(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredSuspension from(Suspension aggregate) {
            return new StoredSuspension(
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

        Suspension toDomain() {
            return Suspension.rehydrate(
                    SuspensionId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    SuspensionStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
