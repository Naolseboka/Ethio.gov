package et.gov.ethgov.organization.adapter.out.persistence;

import et.gov.ethgov.organization.domain.successor.Successor;
import et.gov.ethgov.organization.domain.successor.SuccessorId;
import et.gov.ethgov.organization.domain.successor.SuccessorRepository;
import et.gov.ethgov.organization.domain.successor.SuccessorStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Successor (replaceable with JPA). */
public final class InMemorySuccessorRepository implements SuccessorRepository {
    private final Map<UUID, StoredSuccessor> store = new ConcurrentHashMap<>();

    @Override
    public Successor save(Successor aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredSuccessor.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Successor> findById(SuccessorId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredSuccessor::toDomain);
    }

    @Override
    public Optional<Successor> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredSuccessor::toDomain)
                .findFirst();
    }

    @Override
    public List<Successor> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredSuccessor row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredSuccessor::toDomain)
                .toList();
    }

    @Override
    public void delete(SuccessorId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredSuccessor(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredSuccessor from(Successor aggregate) {
            return new StoredSuccessor(
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

        Successor toDomain() {
            return Successor.rehydrate(
                    SuccessorId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    SuccessorStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
