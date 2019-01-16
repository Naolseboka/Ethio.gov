package et.gov.ethgov.case.adapter.out.persistence;

import et.gov.ethgov.case.domain.decision.Decision;
import et.gov.ethgov.case.domain.decision.DecisionId;
import et.gov.ethgov.case.domain.decision.DecisionRepository;
import et.gov.ethgov.case.domain.decision.DecisionStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Decision (replaceable with JPA). */
public final class InMemoryDecisionRepository implements DecisionRepository {
    private final Map<UUID, StoredDecision> store = new ConcurrentHashMap<>();

    @Override
    public Decision save(Decision aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredDecision.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Decision> findById(DecisionId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredDecision::toDomain);
    }

    @Override
    public Optional<Decision> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredDecision::toDomain)
                .findFirst();
    }

    @Override
    public List<Decision> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredDecision row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredDecision::toDomain)
                .toList();
    }

    @Override
    public void delete(DecisionId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredDecision(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredDecision from(Decision aggregate) {
            return new StoredDecision(
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

        Decision toDomain() {
            return Decision.rehydrate(
                    DecisionId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    DecisionStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
