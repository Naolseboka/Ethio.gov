package et.gov.ethgov.payments.adapter.out.persistence;

import et.gov.ethgov.payments.domain.refund.Refund;
import et.gov.ethgov.payments.domain.refund.RefundId;
import et.gov.ethgov.payments.domain.refund.RefundRepository;
import et.gov.ethgov.payments.domain.refund.RefundStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Refund (replaceable with JPA). */
public final class InMemoryRefundRepository implements RefundRepository {
    private final Map<UUID, StoredRefund> store = new ConcurrentHashMap<>();

    @Override
    public Refund save(Refund aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredRefund.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Refund> findById(RefundId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredRefund::toDomain);
    }

    @Override
    public Optional<Refund> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredRefund::toDomain)
                .findFirst();
    }

    @Override
    public List<Refund> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredRefund row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredRefund::toDomain)
                .toList();
    }

    @Override
    public void delete(RefundId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredRefund(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredRefund from(Refund aggregate) {
            return new StoredRefund(
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

        Refund toDomain() {
            return Refund.rehydrate(
                    RefundId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    RefundStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
