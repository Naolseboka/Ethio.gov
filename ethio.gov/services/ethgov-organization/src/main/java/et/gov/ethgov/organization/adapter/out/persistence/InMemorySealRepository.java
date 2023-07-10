package et.gov.ethgov.organization.adapter.out.persistence;

import et.gov.ethgov.organization.domain.seal.Seal;
import et.gov.ethgov.organization.domain.seal.SealId;
import et.gov.ethgov.organization.domain.seal.SealRepository;
import et.gov.ethgov.organization.domain.seal.SealStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Seal (replaceable with JPA). */
public final class InMemorySealRepository implements SealRepository {
    private final Map<UUID, StoredSeal> store = new ConcurrentHashMap<>();

    @Override
    public Seal save(Seal aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredSeal.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Seal> findById(SealId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredSeal::toDomain);
    }

    @Override
    public Optional<Seal> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredSeal::toDomain)
                .findFirst();
    }

    @Override
    public List<Seal> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredSeal row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredSeal::toDomain)
                .toList();
    }

    @Override
    public void delete(SealId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredSeal(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredSeal from(Seal aggregate) {
            return new StoredSeal(
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

        Seal toDomain() {
            return Seal.rehydrate(
                    SealId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    SealStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
