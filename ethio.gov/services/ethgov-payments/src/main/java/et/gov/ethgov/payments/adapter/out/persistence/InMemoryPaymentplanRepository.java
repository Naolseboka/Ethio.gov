package et.gov.ethgov.payments.adapter.out.persistence;

import et.gov.ethgov.payments.domain.paymentplan.Paymentplan;
import et.gov.ethgov.payments.domain.paymentplan.PaymentplanId;
import et.gov.ethgov.payments.domain.paymentplan.PaymentplanRepository;
import et.gov.ethgov.payments.domain.paymentplan.PaymentplanStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Paymentplan (replaceable with JPA). */
public final class InMemoryPaymentplanRepository implements PaymentplanRepository {
    private final Map<UUID, StoredPaymentplan> store = new ConcurrentHashMap<>();

    @Override
    public Paymentplan save(Paymentplan aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredPaymentplan.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Paymentplan> findById(PaymentplanId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredPaymentplan::toDomain);
    }

    @Override
    public Optional<Paymentplan> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredPaymentplan::toDomain)
                .findFirst();
    }

    @Override
    public List<Paymentplan> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredPaymentplan row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredPaymentplan::toDomain)
                .toList();
    }

    @Override
    public void delete(PaymentplanId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredPaymentplan(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredPaymentplan from(Paymentplan aggregate) {
            return new StoredPaymentplan(
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

        Paymentplan toDomain() {
            return Paymentplan.rehydrate(
                    PaymentplanId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    PaymentplanStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
