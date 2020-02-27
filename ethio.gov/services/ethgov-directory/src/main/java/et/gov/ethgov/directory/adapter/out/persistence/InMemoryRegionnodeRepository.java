package et.gov.ethgov.directory.adapter.out.persistence;

import et.gov.ethgov.directory.domain.regionnode.Regionnode;
import et.gov.ethgov.directory.domain.regionnode.RegionnodeId;
import et.gov.ethgov.directory.domain.regionnode.RegionnodeRepository;
import et.gov.ethgov.directory.domain.regionnode.RegionnodeStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Regionnode (replaceable with JPA). */
public final class InMemoryRegionnodeRepository implements RegionnodeRepository {
    private final Map<UUID, StoredRegionnode> store = new ConcurrentHashMap<>();

    @Override
    public Regionnode save(Regionnode aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredRegionnode.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Regionnode> findById(RegionnodeId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredRegionnode::toDomain);
    }

    @Override
    public Optional<Regionnode> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredRegionnode::toDomain)
                .findFirst();
    }

    @Override
    public List<Regionnode> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredRegionnode row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredRegionnode::toDomain)
                .toList();
    }

    @Override
    public void delete(RegionnodeId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredRegionnode(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredRegionnode from(Regionnode aggregate) {
            return new StoredRegionnode(
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

        Regionnode toDomain() {
            return Regionnode.rehydrate(
                    RegionnodeId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    RegionnodeStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
