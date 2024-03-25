package et.gov.ethgov.permits.adapter.out.persistence;

import et.gov.ethgov.permits.domain.permit.Permit;
import et.gov.ethgov.permits.domain.permit.PermitId;
import et.gov.ethgov.permits.domain.permit.PermitRepository;
import et.gov.ethgov.permits.domain.permit.PermitStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Permit (replaceable with JPA). */
public final class InMemoryPermitRepository implements PermitRepository {
    private final Map<UUID, StoredPermit> store = new ConcurrentHashMap<>();

    @Override
    public Permit save(Permit aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredPermit.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Permit> findById(PermitId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredPermit::toDomain);
    }

    @Override
    public Optional<Permit> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredPermit::toDomain)
                .findFirst();
    }

    @Override
    public List<Permit> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredPermit row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredPermit::toDomain)
                .toList();
    }

    @Override
    public void delete(PermitId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredPermit(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredPermit from(Permit aggregate) {
            return new StoredPermit(
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

        Permit toDomain() {
            return Permit.rehydrate(
                    PermitId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    PermitStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
