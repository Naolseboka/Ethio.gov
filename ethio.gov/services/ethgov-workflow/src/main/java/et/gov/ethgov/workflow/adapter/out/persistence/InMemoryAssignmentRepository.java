package et.gov.ethgov.workflow.adapter.out.persistence;

import et.gov.ethgov.workflow.domain.assignment.Assignment;
import et.gov.ethgov.workflow.domain.assignment.AssignmentId;
import et.gov.ethgov.workflow.domain.assignment.AssignmentRepository;
import et.gov.ethgov.workflow.domain.assignment.AssignmentStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Assignment (replaceable with JPA). */
public final class InMemoryAssignmentRepository implements AssignmentRepository {
    private final Map<UUID, StoredAssignment> store = new ConcurrentHashMap<>();

    @Override
    public Assignment save(Assignment aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredAssignment.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Assignment> findById(AssignmentId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredAssignment::toDomain);
    }

    @Override
    public Optional<Assignment> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredAssignment::toDomain)
                .findFirst();
    }

    @Override
    public List<Assignment> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredAssignment row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredAssignment::toDomain)
                .toList();
    }

    @Override
    public void delete(AssignmentId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredAssignment(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredAssignment from(Assignment aggregate) {
            return new StoredAssignment(
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

        Assignment toDomain() {
            return Assignment.rehydrate(
                    AssignmentId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    AssignmentStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
