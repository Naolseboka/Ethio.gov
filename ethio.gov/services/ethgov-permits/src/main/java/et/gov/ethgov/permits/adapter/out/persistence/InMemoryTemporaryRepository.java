package et.gov.ethgov.permits.adapter.out.persistence;

import et.gov.ethgov.permits.domain.temporary.Temporary;
import et.gov.ethgov.permits.domain.temporary.TemporaryId;
import et.gov.ethgov.permits.domain.temporary.TemporaryRepository;
import et.gov.ethgov.permits.domain.temporary.TemporaryStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Temporary (replaceable with JPA). */
public final class InMemoryTemporaryRepository implements TemporaryRepository {
    private final Map<UUID, StoredTemporary> store = new ConcurrentHashMap<>();

    @Override
    public Temporary save(Temporary aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredTemporary.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Temporary> findById(TemporaryId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredTemporary::toDomain);
    }

    @Override
    public Optional<Temporary> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredTemporary::toDomain)
                .findFirst();
    }

    @Override
    public List<Temporary> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredTemporary row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredTemporary::toDomain)
                .toList();
    }

    @Override
    public void delete(TemporaryId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredTemporary(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredTemporary from(Temporary aggregate) {
            return new StoredTemporary(
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

        Temporary toDomain() {
            return Temporary.rehydrate(
                    TemporaryId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    TemporaryStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
