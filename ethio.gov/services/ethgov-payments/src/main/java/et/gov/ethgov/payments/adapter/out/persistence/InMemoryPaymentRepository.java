package et.gov.ethgov.payments.adapter.out.persistence;

import et.gov.ethgov.payments.domain.payment.Payment;
import et.gov.ethgov.payments.domain.payment.PaymentId;
import et.gov.ethgov.payments.domain.payment.PaymentRepository;
import et.gov.ethgov.payments.domain.payment.PaymentStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Payment (replaceable with JPA). */
public final class InMemoryPaymentRepository implements PaymentRepository {
    private final Map<UUID, StoredPayment> store = new ConcurrentHashMap<>();

    @Override
    public Payment save(Payment aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredPayment.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Payment> findById(PaymentId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredPayment::toDomain);
    }

    @Override
    public Optional<Payment> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredPayment::toDomain)
                .findFirst();
    }

    @Override
    public List<Payment> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredPayment row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredPayment::toDomain)
                .toList();
    }

    @Override
    public void delete(PaymentId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredPayment(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredPayment from(Payment aggregate) {
            return new StoredPayment(
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

        Payment toDomain() {
            return Payment.rehydrate(
                    PaymentId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    PaymentStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
