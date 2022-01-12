package et.gov.ethgov.land.adapter.out.persistence;

import et.gov.ethgov.land.domain.easement.Easement;
import et.gov.ethgov.land.domain.easement.EasementId;
import et.gov.ethgov.land.domain.easement.EasementRepository;
import et.gov.ethgov.land.domain.easement.EasementStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Easement (replaceable with JPA). */
public final class InMemoryEasementRepository implements EasementRepository {
    private final Map<UUID, StoredEasement> store = new ConcurrentHashMap<>();

    @Override
    public Easement save(Easement aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredEasement.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Easement> findById(EasementId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredEasement::toDomain);
    }

    @Override
    public Optional<Easement> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredEasement::toDomain)
                .findFirst();
    }

    @Override
    public List<Easement> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredEasement row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredEasement::toDomain)
                .toList();
    }

    @Override
    public void delete(EasementId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredEasement(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredEasement from(Easement aggregate) {
            return new StoredEasement(
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

        Easement toDomain() {
            return Easement.rehydrate(
                    EasementId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    EasementStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
