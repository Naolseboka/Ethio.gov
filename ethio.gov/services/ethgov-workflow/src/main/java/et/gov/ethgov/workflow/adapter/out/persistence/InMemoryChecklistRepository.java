package et.gov.ethgov.workflow.adapter.out.persistence;

import et.gov.ethgov.workflow.domain.checklist.Checklist;
import et.gov.ethgov.workflow.domain.checklist.ChecklistId;
import et.gov.ethgov.workflow.domain.checklist.ChecklistRepository;
import et.gov.ethgov.workflow.domain.checklist.ChecklistStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Checklist (replaceable with JPA). */
public final class InMemoryChecklistRepository implements ChecklistRepository {
    private final Map<UUID, StoredChecklist> store = new ConcurrentHashMap<>();

    @Override
    public Checklist save(Checklist aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredChecklist.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Checklist> findById(ChecklistId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredChecklist::toDomain);
    }

    @Override
    public Optional<Checklist> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredChecklist::toDomain)
                .findFirst();
    }

    @Override
    public List<Checklist> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredChecklist row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredChecklist::toDomain)
                .toList();
    }

    @Override
    public void delete(ChecklistId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredChecklist(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredChecklist from(Checklist aggregate) {
            return new StoredChecklist(
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

        Checklist toDomain() {
            return Checklist.rehydrate(
                    ChecklistId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    ChecklistStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
