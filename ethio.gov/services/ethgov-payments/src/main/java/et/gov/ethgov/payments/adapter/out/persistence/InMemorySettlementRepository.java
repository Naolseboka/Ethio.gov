package et.gov.ethgov.payments.adapter.out.persistence;

import et.gov.ethgov.payments.domain.settlement.Settlement;
import et.gov.ethgov.payments.domain.settlement.SettlementId;
import et.gov.ethgov.payments.domain.settlement.SettlementRepository;
import et.gov.ethgov.payments.domain.settlement.SettlementStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Settlement (replaceable with JPA). */
public final class InMemorySettlementRepository implements SettlementRepository {
    private final Map<UUID, StoredSettlement> store = new ConcurrentHashMap<>();

    @Override
    public Settlement save(Settlement aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredSettlement.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Settlement> findById(SettlementId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredSettlement::toDomain);
    }

    @Override
    public Optional<Settlement> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredSettlement::toDomain)
                .findFirst();
    }

    @Override
    public List<Settlement> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredSettlement row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredSettlement::toDomain)
                .toList();
    }

    @Override
    public void delete(SettlementId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredSettlement(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredSettlement from(Settlement aggregate) {
            return new StoredSettlement(
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

        Settlement toDomain() {
            return Settlement.rehydrate(
                    SettlementId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    SettlementStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
