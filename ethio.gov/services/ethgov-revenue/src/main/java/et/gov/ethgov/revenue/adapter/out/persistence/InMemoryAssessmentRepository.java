package et.gov.ethgov.revenue.adapter.out.persistence;

import et.gov.ethgov.revenue.domain.assessment.Assessment;
import et.gov.ethgov.revenue.domain.assessment.AssessmentId;
import et.gov.ethgov.revenue.domain.assessment.AssessmentRepository;
import et.gov.ethgov.revenue.domain.assessment.AssessmentStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Assessment (replaceable with JPA). */
public final class InMemoryAssessmentRepository implements AssessmentRepository {
    private final Map<UUID, StoredAssessment> store = new ConcurrentHashMap<>();

    @Override
    public Assessment save(Assessment aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredAssessment.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Assessment> findById(AssessmentId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredAssessment::toDomain);
    }

    @Override
    public Optional<Assessment> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredAssessment::toDomain)
                .findFirst();
    }

    @Override
    public List<Assessment> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredAssessment row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredAssessment::toDomain)
                .toList();
    }

    @Override
    public void delete(AssessmentId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredAssessment(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredAssessment from(Assessment aggregate) {
            return new StoredAssessment(
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

        Assessment toDomain() {
            return Assessment.rehydrate(
                    AssessmentId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    AssessmentStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
