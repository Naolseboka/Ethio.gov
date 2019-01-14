package et.gov.ethgov.case.adapter.out.persistence;

import et.gov.ethgov.case.domain.caseassignment.Caseassignment;
import et.gov.ethgov.case.domain.caseassignment.CaseassignmentId;
import et.gov.ethgov.case.domain.caseassignment.CaseassignmentRepository;
import et.gov.ethgov.case.domain.caseassignment.CaseassignmentStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Caseassignment (replaceable with JPA). */
public final class InMemoryCaseassignmentRepository implements CaseassignmentRepository {
    private final Map<UUID, StoredCaseassignment> store = new ConcurrentHashMap<>();

    @Override
    public Caseassignment save(Caseassignment aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredCaseassignment.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Caseassignment> findById(CaseassignmentId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredCaseassignment::toDomain);
    }

    @Override
    public Optional<Caseassignment> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredCaseassignment::toDomain)
                .findFirst();
    }

    @Override
    public List<Caseassignment> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredCaseassignment row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredCaseassignment::toDomain)
                .toList();
    }

    @Override
    public void delete(CaseassignmentId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredCaseassignment(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredCaseassignment from(Caseassignment aggregate) {
            return new StoredCaseassignment(
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

        Caseassignment toDomain() {
            return Caseassignment.rehydrate(
                    CaseassignmentId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    CaseassignmentStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
