package et.gov.ethgov.search.adapter.out.persistence;

import et.gov.ethgov.search.domain.boost.Boost;
import et.gov.ethgov.search.domain.boost.BoostId;
import et.gov.ethgov.search.domain.boost.BoostRepository;
import et.gov.ethgov.search.domain.boost.BoostStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Boost (replaceable with JPA). */
public final class InMemoryBoostRepository implements BoostRepository {
    private final Map<UUID, StoredBoost> store = new ConcurrentHashMap<>();

    @Override
    public Boost save(Boost aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredBoost.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Boost> findById(BoostId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredBoost::toDomain);
    }

    @Override
    public Optional<Boost> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredBoost::toDomain)
                .findFirst();
    }

    @Override
    public List<Boost> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredBoost row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredBoost::toDomain)
                .toList();
    }

    @Override
    public void delete(BoostId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredBoost(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredBoost from(Boost aggregate) {
            return new StoredBoost(
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

        Boost toDomain() {
            return Boost.rehydrate(
                    BoostId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    BoostStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
