package et.gov.ethgov.workflow.adapter.out.persistence;

import et.gov.ethgov.workflow.domain.definition.Definition;
import et.gov.ethgov.workflow.domain.definition.DefinitionId;
import et.gov.ethgov.workflow.domain.definition.DefinitionRepository;
import et.gov.ethgov.workflow.domain.definition.DefinitionStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Definition (replaceable with JPA). */
public final class InMemoryDefinitionRepository implements DefinitionRepository {
    private final Map<UUID, StoredDefinition> store = new ConcurrentHashMap<>();

    @Override
    public Definition save(Definition aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredDefinition.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Definition> findById(DefinitionId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredDefinition::toDomain);
    }

    @Override
    public Optional<Definition> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredDefinition::toDomain)
                .findFirst();
    }

    @Override
    public List<Definition> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredDefinition row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredDefinition::toDomain)
                .toList();
    }

    @Override
    public void delete(DefinitionId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredDefinition(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredDefinition from(Definition aggregate) {
            return new StoredDefinition(
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

        Definition toDomain() {
            return Definition.rehydrate(
                    DefinitionId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    DefinitionStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
