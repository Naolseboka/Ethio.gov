package et.gov.ethgov.revenue.adapter.out.persistence;

import et.gov.ethgov.revenue.domain.installment.Installment;
import et.gov.ethgov.revenue.domain.installment.InstallmentId;
import et.gov.ethgov.revenue.domain.installment.InstallmentRepository;
import et.gov.ethgov.revenue.domain.installment.InstallmentStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Installment (replaceable with JPA). */
public final class InMemoryInstallmentRepository implements InstallmentRepository {
    private final Map<UUID, StoredInstallment> store = new ConcurrentHashMap<>();

    @Override
    public Installment save(Installment aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredInstallment.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Installment> findById(InstallmentId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredInstallment::toDomain);
    }

    @Override
    public Optional<Installment> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredInstallment::toDomain)
                .findFirst();
    }

    @Override
    public List<Installment> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredInstallment row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredInstallment::toDomain)
                .toList();
    }

    @Override
    public void delete(InstallmentId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredInstallment(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredInstallment from(Installment aggregate) {
            return new StoredInstallment(
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

        Installment toDomain() {
            return Installment.rehydrate(
                    InstallmentId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    InstallmentStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
