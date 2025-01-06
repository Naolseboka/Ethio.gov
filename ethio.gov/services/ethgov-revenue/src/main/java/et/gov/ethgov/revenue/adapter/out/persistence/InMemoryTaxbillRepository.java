package et.gov.ethgov.revenue.adapter.out.persistence;

import et.gov.ethgov.revenue.domain.taxbill.Taxbill;
import et.gov.ethgov.revenue.domain.taxbill.TaxbillId;
import et.gov.ethgov.revenue.domain.taxbill.TaxbillRepository;
import et.gov.ethgov.revenue.domain.taxbill.TaxbillStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Taxbill (replaceable with JPA). */
public final class InMemoryTaxbillRepository implements TaxbillRepository {
    private final Map<UUID, StoredTaxbill> store = new ConcurrentHashMap<>();

    @Override
    public Taxbill save(Taxbill aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredTaxbill.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Taxbill> findById(TaxbillId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredTaxbill::toDomain);
    }

    @Override
    public Optional<Taxbill> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredTaxbill::toDomain)
                .findFirst();
    }

    @Override
    public List<Taxbill> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredTaxbill row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredTaxbill::toDomain)
                .toList();
    }

    @Override
    public void delete(TaxbillId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredTaxbill(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredTaxbill from(Taxbill aggregate) {
            return new StoredTaxbill(
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

        Taxbill toDomain() {
            return Taxbill.rehydrate(
                    TaxbillId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    TaxbillStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
