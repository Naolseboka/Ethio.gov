package et.gov.ethgov.workflow.adapter.out.persistence;

import et.gov.ethgov.workflow.domain.transition.Transition;
import et.gov.ethgov.workflow.domain.transition.TransitionId;
import et.gov.ethgov.workflow.domain.transition.TransitionRepository;
import et.gov.ethgov.workflow.domain.transition.TransitionStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Transition (replaceable with JPA). */
public final class InMemoryTransitionRepository implements TransitionRepository {
    private final Map<UUID, StoredTransition> store = new ConcurrentHashMap<>();

    @Override
    public Transition save(Transition aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredTransition.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Transition> findById(TransitionId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredTransition::toDomain);
    }

    @Override
    public Optional<Transition> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredTransition::toDomain)
                .findFirst();
    }

    @Override
    public List<Transition> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredTransition row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredTransition::toDomain)
                .toList();
    }

    @Override
    public void delete(TransitionId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredTransition(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredTransition from(Transition aggregate) {
            return new StoredTransition(
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

        Transition toDomain() {
            return Transition.rehydrate(
                    TransitionId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    TransitionStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
