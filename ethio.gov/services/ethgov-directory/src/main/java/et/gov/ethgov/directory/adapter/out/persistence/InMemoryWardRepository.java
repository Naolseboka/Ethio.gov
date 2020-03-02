package et.gov.ethgov.directory.adapter.out.persistence;

import et.gov.ethgov.directory.domain.ward.Ward;
import et.gov.ethgov.directory.domain.ward.WardId;
import et.gov.ethgov.directory.domain.ward.WardRepository;
import et.gov.ethgov.directory.domain.ward.WardStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Ward (replaceable with JPA). */
public final class InMemoryWardRepository implements WardRepository {
    private final Map<UUID, StoredWard> store = new ConcurrentHashMap<>();

    @Override
    public Ward save(Ward aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredWard.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Ward> findById(WardId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredWard::toDomain);
    }

    @Override
    public Optional<Ward> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredWard::toDomain)
                .findFirst();
    }

    @Override
    public List<Ward> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredWard row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredWard::toDomain)
                .toList();
    }

    @Override
    public void delete(WardId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredWard(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredWard from(Ward aggregate) {
            return new StoredWard(
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

        Ward toDomain() {
            return Ward.rehydrate(
                    WardId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    WardStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
