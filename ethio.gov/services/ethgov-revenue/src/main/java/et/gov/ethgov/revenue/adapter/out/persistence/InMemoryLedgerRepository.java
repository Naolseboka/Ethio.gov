package et.gov.ethgov.revenue.adapter.out.persistence;

import et.gov.ethgov.revenue.domain.ledger.Ledger;
import et.gov.ethgov.revenue.domain.ledger.LedgerId;
import et.gov.ethgov.revenue.domain.ledger.LedgerRepository;
import et.gov.ethgov.revenue.domain.ledger.LedgerStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Ledger (replaceable with JPA). */
public final class InMemoryLedgerRepository implements LedgerRepository {
    private final Map<UUID, StoredLedger> store = new ConcurrentHashMap<>();

    @Override
    public Ledger save(Ledger aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredLedger.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Ledger> findById(LedgerId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredLedger::toDomain);
    }

    @Override
    public Optional<Ledger> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredLedger::toDomain)
                .findFirst();
    }

    @Override
    public List<Ledger> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredLedger row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredLedger::toDomain)
                .toList();
    }

    @Override
    public void delete(LedgerId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredLedger(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredLedger from(Ledger aggregate) {
            return new StoredLedger(
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

        Ledger toDomain() {
            return Ledger.rehydrate(
                    LedgerId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    LedgerStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
