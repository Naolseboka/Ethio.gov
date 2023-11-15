package et.gov.ethgov.payments.adapter.out.persistence;

import et.gov.ethgov.payments.domain.invoice.Invoice;
import et.gov.ethgov.payments.domain.invoice.InvoiceId;
import et.gov.ethgov.payments.domain.invoice.InvoiceRepository;
import et.gov.ethgov.payments.domain.invoice.InvoiceStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Invoice (replaceable with JPA). */
public final class InMemoryInvoiceRepository implements InvoiceRepository {
    private final Map<UUID, StoredInvoice> store = new ConcurrentHashMap<>();

    @Override
    public Invoice save(Invoice aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredInvoice.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Invoice> findById(InvoiceId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredInvoice::toDomain);
    }

    @Override
    public Optional<Invoice> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredInvoice::toDomain)
                .findFirst();
    }

    @Override
    public List<Invoice> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredInvoice row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredInvoice::toDomain)
                .toList();
    }

    @Override
    public void delete(InvoiceId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredInvoice(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredInvoice from(Invoice aggregate) {
            return new StoredInvoice(
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

        Invoice toDomain() {
            return Invoice.rehydrate(
                    InvoiceId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    InvoiceStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
