package et.gov.ethgov.analytics.adapter.out.persistence;

import et.gov.ethgov.analytics.domain.cohort.Cohort;
import et.gov.ethgov.analytics.domain.cohort.CohortId;
import et.gov.ethgov.analytics.domain.cohort.CohortRepository;
import et.gov.ethgov.analytics.domain.cohort.CohortStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Cohort (replaceable with JPA). */
public final class InMemoryCohortRepository implements CohortRepository {
    private final Map<UUID, StoredCohort> store = new ConcurrentHashMap<>();

    @Override
    public Cohort save(Cohort aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredCohort.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Cohort> findById(CohortId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredCohort::toDomain);
    }

    @Override
    public Optional<Cohort> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredCohort::toDomain)
                .findFirst();
    }

    @Override
    public List<Cohort> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredCohort row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredCohort::toDomain)
                .toList();
    }

    @Override
    public void delete(CohortId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredCohort(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredCohort from(Cohort aggregate) {
            return new StoredCohort(
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

        Cohort toDomain() {
            return Cohort.rehydrate(
                    CohortId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    CohortStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
