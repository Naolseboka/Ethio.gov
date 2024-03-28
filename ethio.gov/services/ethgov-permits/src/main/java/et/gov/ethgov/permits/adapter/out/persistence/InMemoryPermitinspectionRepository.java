package et.gov.ethgov.permits.adapter.out.persistence;

import et.gov.ethgov.permits.domain.permitinspection.Permitinspection;
import et.gov.ethgov.permits.domain.permitinspection.PermitinspectionId;
import et.gov.ethgov.permits.domain.permitinspection.PermitinspectionRepository;
import et.gov.ethgov.permits.domain.permitinspection.PermitinspectionStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Permitinspection (replaceable with JPA). */
public final class InMemoryPermitinspectionRepository implements PermitinspectionRepository {
    private final Map<UUID, StoredPermitinspection> store = new ConcurrentHashMap<>();

    @Override
    public Permitinspection save(Permitinspection aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredPermitinspection.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Permitinspection> findById(PermitinspectionId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredPermitinspection::toDomain);
    }

    @Override
    public Optional<Permitinspection> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredPermitinspection::toDomain)
                .findFirst();
    }

    @Override
    public List<Permitinspection> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredPermitinspection row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredPermitinspection::toDomain)
                .toList();
    }

    @Override
    public void delete(PermitinspectionId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredPermitinspection(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredPermitinspection from(Permitinspection aggregate) {
            return new StoredPermitinspection(
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

        Permitinspection toDomain() {
            return Permitinspection.rehydrate(
                    PermitinspectionId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    PermitinspectionStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
