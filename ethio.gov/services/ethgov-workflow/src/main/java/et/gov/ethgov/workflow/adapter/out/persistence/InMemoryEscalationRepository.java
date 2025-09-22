package et.gov.ethgov.workflow.adapter.out.persistence;

import et.gov.ethgov.workflow.domain.escalation.Escalation;
import et.gov.ethgov.workflow.domain.escalation.EscalationId;
import et.gov.ethgov.workflow.domain.escalation.EscalationRepository;
import et.gov.ethgov.workflow.domain.escalation.EscalationStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Escalation (replaceable with JPA). */
public final class InMemoryEscalationRepository implements EscalationRepository {
    private final Map<UUID, StoredEscalation> store = new ConcurrentHashMap<>();

    @Override
    public Escalation save(Escalation aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredEscalation.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Escalation> findById(EscalationId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredEscalation::toDomain);
    }

    @Override
    public Optional<Escalation> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredEscalation::toDomain)
                .findFirst();
    }

    @Override
    public List<Escalation> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredEscalation row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredEscalation::toDomain)
                .toList();
    }

    @Override
    public void delete(EscalationId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredEscalation(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredEscalation from(Escalation aggregate) {
            return new StoredEscalation(
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

        Escalation toDomain() {
            return Escalation.rehydrate(
                    EscalationId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    EscalationStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
