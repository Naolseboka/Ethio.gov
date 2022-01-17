package et.gov.ethgov.land.adapter.out.persistence;

import et.gov.ethgov.land.domain.transfer.Transfer;
import et.gov.ethgov.land.domain.transfer.TransferId;
import et.gov.ethgov.land.domain.transfer.TransferRepository;
import et.gov.ethgov.land.domain.transfer.TransferStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Transfer (replaceable with JPA). */
public final class InMemoryTransferRepository implements TransferRepository {
    private final Map<UUID, StoredTransfer> store = new ConcurrentHashMap<>();

    @Override
    public Transfer save(Transfer aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredTransfer.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Transfer> findById(TransferId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredTransfer::toDomain);
    }

    @Override
    public Optional<Transfer> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredTransfer::toDomain)
                .findFirst();
    }

    @Override
    public List<Transfer> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredTransfer row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredTransfer::toDomain)
                .toList();
    }

    @Override
    public void delete(TransferId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredTransfer(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredTransfer from(Transfer aggregate) {
            return new StoredTransfer(
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

        Transfer toDomain() {
            return Transfer.rehydrate(
                    TransferId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    TransferStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
