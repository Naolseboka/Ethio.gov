package et.gov.ethgov.licensing.adapter.out.persistence;

import et.gov.ethgov.licensing.domain.inspection.Inspection;
import et.gov.ethgov.licensing.domain.inspection.InspectionId;
import et.gov.ethgov.licensing.domain.inspection.InspectionRepository;
import et.gov.ethgov.licensing.domain.inspection.InspectionStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Inspection (replaceable with JPA). */
public final class InMemoryInspectionRepository implements InspectionRepository {
    private final Map<UUID, StoredInspection> store = new ConcurrentHashMap<>();

    @Override
    public Inspection save(Inspection aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredInspection.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Inspection> findById(InspectionId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredInspection::toDomain);
    }

    @Override
    public Optional<Inspection> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredInspection::toDomain)
                .findFirst();
    }

    @Override
    public List<Inspection> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredInspection row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredInspection::toDomain)
                .toList();
    }

    @Override
    public void delete(InspectionId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredInspection(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredInspection from(Inspection aggregate) {
            return new StoredInspection(
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

        Inspection toDomain() {
            return Inspection.rehydrate(
                    InspectionId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    InspectionStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
