package et.gov.ethgov.permits.adapter.out.persistence;

import et.gov.ethgov.permits.domain.permitchecklist.Permitchecklist;
import et.gov.ethgov.permits.domain.permitchecklist.PermitchecklistId;
import et.gov.ethgov.permits.domain.permitchecklist.PermitchecklistRepository;
import et.gov.ethgov.permits.domain.permitchecklist.PermitchecklistStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Permitchecklist (replaceable with JPA). */
public final class InMemoryPermitchecklistRepository implements PermitchecklistRepository {
    private final Map<UUID, StoredPermitchecklist> store = new ConcurrentHashMap<>();

    @Override
    public Permitchecklist save(Permitchecklist aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredPermitchecklist.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Permitchecklist> findById(PermitchecklistId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredPermitchecklist::toDomain);
    }

    @Override
    public Optional<Permitchecklist> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredPermitchecklist::toDomain)
                .findFirst();
    }

    @Override
    public List<Permitchecklist> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredPermitchecklist row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredPermitchecklist::toDomain)
                .toList();
    }

    @Override
    public void delete(PermitchecklistId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredPermitchecklist(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredPermitchecklist from(Permitchecklist aggregate) {
            return new StoredPermitchecklist(
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

        Permitchecklist toDomain() {
            return Permitchecklist.rehydrate(
                    PermitchecklistId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    PermitchecklistStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
