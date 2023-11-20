package et.gov.ethgov.payments.adapter.out.persistence;

import et.gov.ethgov.payments.domain.wallet.Wallet;
import et.gov.ethgov.payments.domain.wallet.WalletId;
import et.gov.ethgov.payments.domain.wallet.WalletRepository;
import et.gov.ethgov.payments.domain.wallet.WalletStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Wallet (replaceable with JPA). */
public final class InMemoryWalletRepository implements WalletRepository {
    private final Map<UUID, StoredWallet> store = new ConcurrentHashMap<>();

    @Override
    public Wallet save(Wallet aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredWallet.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Wallet> findById(WalletId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredWallet::toDomain);
    }

    @Override
    public Optional<Wallet> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredWallet::toDomain)
                .findFirst();
    }

    @Override
    public List<Wallet> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredWallet row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredWallet::toDomain)
                .toList();
    }

    @Override
    public void delete(WalletId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredWallet(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredWallet from(Wallet aggregate) {
            return new StoredWallet(
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

        Wallet toDomain() {
            return Wallet.rehydrate(
                    WalletId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    WalletStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
