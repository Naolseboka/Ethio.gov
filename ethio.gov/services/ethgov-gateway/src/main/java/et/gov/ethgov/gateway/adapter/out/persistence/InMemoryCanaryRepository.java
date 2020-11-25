package et.gov.ethgov.gateway.adapter.out.persistence;

import et.gov.ethgov.gateway.domain.canary.Canary;
import et.gov.ethgov.gateway.domain.canary.CanaryId;
import et.gov.ethgov.gateway.domain.canary.CanaryRepository;
import et.gov.ethgov.gateway.domain.canary.CanaryStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Canary (replaceable with JPA). */
public final class InMemoryCanaryRepository implements CanaryRepository {
    private final Map<UUID, StoredCanary> store = new ConcurrentHashMap<>();

    @Override
    public Canary save(Canary aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredCanary.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Canary> findById(CanaryId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredCanary::toDomain);
    }

    @Override
    public Optional<Canary> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredCanary::toDomain)
                .findFirst();
    }

    @Override
    public List<Canary> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredCanary row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredCanary::toDomain)
                .toList();
    }

    @Override
    public void delete(CanaryId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredCanary(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredCanary from(Canary aggregate) {
            return new StoredCanary(
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

        Canary toDomain() {
            return Canary.rehydrate(
                    CanaryId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    CanaryStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
