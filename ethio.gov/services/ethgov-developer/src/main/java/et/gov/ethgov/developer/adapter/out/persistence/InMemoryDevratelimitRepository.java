package et.gov.ethgov.developer.adapter.out.persistence;

import et.gov.ethgov.developer.domain.devratelimit.Devratelimit;
import et.gov.ethgov.developer.domain.devratelimit.DevratelimitId;
import et.gov.ethgov.developer.domain.devratelimit.DevratelimitRepository;
import et.gov.ethgov.developer.domain.devratelimit.DevratelimitStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Devratelimit (replaceable with JPA). */
public final class InMemoryDevratelimitRepository implements DevratelimitRepository {
    private final Map<UUID, StoredDevratelimit> store = new ConcurrentHashMap<>();

    @Override
    public Devratelimit save(Devratelimit aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredDevratelimit.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Devratelimit> findById(DevratelimitId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredDevratelimit::toDomain);
    }

    @Override
    public Optional<Devratelimit> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredDevratelimit::toDomain)
                .findFirst();
    }

    @Override
    public List<Devratelimit> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredDevratelimit row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredDevratelimit::toDomain)
                .toList();
    }

    @Override
    public void delete(DevratelimitId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredDevratelimit(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredDevratelimit from(Devratelimit aggregate) {
            return new StoredDevratelimit(
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

        Devratelimit toDomain() {
            return Devratelimit.rehydrate(
                    DevratelimitId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    DevratelimitStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
