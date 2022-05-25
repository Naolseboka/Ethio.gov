package et.gov.ethgov.licensing.adapter.out.persistence;

import et.gov.ethgov.licensing.domain.holder.Holder;
import et.gov.ethgov.licensing.domain.holder.HolderId;
import et.gov.ethgov.licensing.domain.holder.HolderRepository;
import et.gov.ethgov.licensing.domain.holder.HolderStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Holder (replaceable with JPA). */
public final class InMemoryHolderRepository implements HolderRepository {
    private final Map<UUID, StoredHolder> store = new ConcurrentHashMap<>();

    @Override
    public Holder save(Holder aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredHolder.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Holder> findById(HolderId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredHolder::toDomain);
    }

    @Override
    public Optional<Holder> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredHolder::toDomain)
                .findFirst();
    }

    @Override
    public List<Holder> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredHolder row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredHolder::toDomain)
                .toList();
    }

    @Override
    public void delete(HolderId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredHolder(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredHolder from(Holder aggregate) {
            return new StoredHolder(
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

        Holder toDomain() {
            return Holder.rehydrate(
                    HolderId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    HolderStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
