package et.gov.ethgov.revenue.adapter.out.persistence;

import et.gov.ethgov.revenue.domain.exemption.Exemption;
import et.gov.ethgov.revenue.domain.exemption.ExemptionId;
import et.gov.ethgov.revenue.domain.exemption.ExemptionRepository;
import et.gov.ethgov.revenue.domain.exemption.ExemptionStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Exemption (replaceable with JPA). */
public final class InMemoryExemptionRepository implements ExemptionRepository {
    private final Map<UUID, StoredExemption> store = new ConcurrentHashMap<>();

    @Override
    public Exemption save(Exemption aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredExemption.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Exemption> findById(ExemptionId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredExemption::toDomain);
    }

    @Override
    public Optional<Exemption> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredExemption::toDomain)
                .findFirst();
    }

    @Override
    public List<Exemption> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredExemption row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredExemption::toDomain)
                .toList();
    }

    @Override
    public void delete(ExemptionId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredExemption(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredExemption from(Exemption aggregate) {
            return new StoredExemption(
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

        Exemption toDomain() {
            return Exemption.rehydrate(
                    ExemptionId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    ExemptionStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
