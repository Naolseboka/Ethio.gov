package et.gov.ethgov.payments.adapter.out.persistence;

import et.gov.ethgov.payments.domain.receipt.Receipt;
import et.gov.ethgov.payments.domain.receipt.ReceiptId;
import et.gov.ethgov.payments.domain.receipt.ReceiptRepository;
import et.gov.ethgov.payments.domain.receipt.ReceiptStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Receipt (replaceable with JPA). */
public final class InMemoryReceiptRepository implements ReceiptRepository {
    private final Map<UUID, StoredReceipt> store = new ConcurrentHashMap<>();

    @Override
    public Receipt save(Receipt aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredReceipt.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Receipt> findById(ReceiptId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredReceipt::toDomain);
    }

    @Override
    public Optional<Receipt> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredReceipt::toDomain)
                .findFirst();
    }

    @Override
    public List<Receipt> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredReceipt row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredReceipt::toDomain)
                .toList();
    }

    @Override
    public void delete(ReceiptId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredReceipt(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredReceipt from(Receipt aggregate) {
            return new StoredReceipt(
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

        Receipt toDomain() {
            return Receipt.rehydrate(
                    ReceiptId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    ReceiptStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
