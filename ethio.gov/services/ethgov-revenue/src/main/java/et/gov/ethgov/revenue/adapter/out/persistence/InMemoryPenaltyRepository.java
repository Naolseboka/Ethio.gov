package et.gov.ethgov.revenue.adapter.out.persistence;

import et.gov.ethgov.revenue.domain.penalty.Penalty;
import et.gov.ethgov.revenue.domain.penalty.PenaltyId;
import et.gov.ethgov.revenue.domain.penalty.PenaltyRepository;
import et.gov.ethgov.revenue.domain.penalty.PenaltyStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Penalty (replaceable with JPA). */
public final class InMemoryPenaltyRepository implements PenaltyRepository {
    private final Map<UUID, StoredPenalty> store = new ConcurrentHashMap<>();

    @Override
    public Penalty save(Penalty aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredPenalty.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Penalty> findById(PenaltyId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredPenalty::toDomain);
    }

    @Override
    public Optional<Penalty> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredPenalty::toDomain)
                .findFirst();
    }

    @Override
    public List<Penalty> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredPenalty row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredPenalty::toDomain)
                .toList();
    }

    @Override
    public void delete(PenaltyId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredPenalty(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredPenalty from(Penalty aggregate) {
            return new StoredPenalty(
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

        Penalty toDomain() {
            return Penalty.rehydrate(
                    PenaltyId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    PenaltyStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
