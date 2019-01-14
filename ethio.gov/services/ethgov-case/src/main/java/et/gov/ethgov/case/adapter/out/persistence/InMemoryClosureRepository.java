package et.gov.ethgov.case.adapter.out.persistence;

import et.gov.ethgov.case.domain.closure.Closure;
import et.gov.ethgov.case.domain.closure.ClosureId;
import et.gov.ethgov.case.domain.closure.ClosureRepository;
import et.gov.ethgov.case.domain.closure.ClosureStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Closure (replaceable with JPA). */
public final class InMemoryClosureRepository implements ClosureRepository {
    private final Map<UUID, StoredClosure> store = new ConcurrentHashMap<>();

    @Override
    public Closure save(Closure aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredClosure.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Closure> findById(ClosureId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredClosure::toDomain);
    }

    @Override
    public Optional<Closure> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredClosure::toDomain)
                .findFirst();
    }

    @Override
    public List<Closure> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredClosure row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredClosure::toDomain)
                .toList();
    }

    @Override
    public void delete(ClosureId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredClosure(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredClosure from(Closure aggregate) {
            return new StoredClosure(
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

        Closure toDomain() {
            return Closure.rehydrate(
                    ClosureId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    ClosureStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
