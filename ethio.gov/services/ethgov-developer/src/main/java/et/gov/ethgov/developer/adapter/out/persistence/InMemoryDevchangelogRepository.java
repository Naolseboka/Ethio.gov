package et.gov.ethgov.developer.adapter.out.persistence;

import et.gov.ethgov.developer.domain.devchangelog.Devchangelog;
import et.gov.ethgov.developer.domain.devchangelog.DevchangelogId;
import et.gov.ethgov.developer.domain.devchangelog.DevchangelogRepository;
import et.gov.ethgov.developer.domain.devchangelog.DevchangelogStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Devchangelog (replaceable with JPA). */
public final class InMemoryDevchangelogRepository implements DevchangelogRepository {
    private final Map<UUID, StoredDevchangelog> store = new ConcurrentHashMap<>();

    @Override
    public Devchangelog save(Devchangelog aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredDevchangelog.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Devchangelog> findById(DevchangelogId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredDevchangelog::toDomain);
    }

    @Override
    public Optional<Devchangelog> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredDevchangelog::toDomain)
                .findFirst();
    }

    @Override
    public List<Devchangelog> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredDevchangelog row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredDevchangelog::toDomain)
                .toList();
    }

    @Override
    public void delete(DevchangelogId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredDevchangelog(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredDevchangelog from(Devchangelog aggregate) {
            return new StoredDevchangelog(
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

        Devchangelog toDomain() {
            return Devchangelog.rehydrate(
                    DevchangelogId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    DevchangelogStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
