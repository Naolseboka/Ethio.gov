package et.gov.ethgov.payments.adapter.out.persistence;

import et.gov.ethgov.payments.domain.chargeback.Chargeback;
import et.gov.ethgov.payments.domain.chargeback.ChargebackId;
import et.gov.ethgov.payments.domain.chargeback.ChargebackRepository;
import et.gov.ethgov.payments.domain.chargeback.ChargebackStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Chargeback (replaceable with JPA). */
public final class InMemoryChargebackRepository implements ChargebackRepository {
    private final Map<UUID, StoredChargeback> store = new ConcurrentHashMap<>();

    @Override
    public Chargeback save(Chargeback aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredChargeback.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Chargeback> findById(ChargebackId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredChargeback::toDomain);
    }

    @Override
    public Optional<Chargeback> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredChargeback::toDomain)
                .findFirst();
    }

    @Override
    public List<Chargeback> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredChargeback row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredChargeback::toDomain)
                .toList();
    }

    @Override
    public void delete(ChargebackId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredChargeback(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredChargeback from(Chargeback aggregate) {
            return new StoredChargeback(
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

        Chargeback toDomain() {
            return Chargeback.rehydrate(
                    ChargebackId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    ChargebackStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
