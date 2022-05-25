package et.gov.ethgov.licensing.adapter.out.persistence;

import et.gov.ethgov.licensing.domain.compliance.Compliance;
import et.gov.ethgov.licensing.domain.compliance.ComplianceId;
import et.gov.ethgov.licensing.domain.compliance.ComplianceRepository;
import et.gov.ethgov.licensing.domain.compliance.ComplianceStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Compliance (replaceable with JPA). */
public final class InMemoryComplianceRepository implements ComplianceRepository {
    private final Map<UUID, StoredCompliance> store = new ConcurrentHashMap<>();

    @Override
    public Compliance save(Compliance aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredCompliance.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Compliance> findById(ComplianceId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredCompliance::toDomain);
    }

    @Override
    public Optional<Compliance> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredCompliance::toDomain)
                .findFirst();
    }

    @Override
    public List<Compliance> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredCompliance row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredCompliance::toDomain)
                .toList();
    }

    @Override
    public void delete(ComplianceId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredCompliance(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredCompliance from(Compliance aggregate) {
            return new StoredCompliance(
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

        Compliance toDomain() {
            return Compliance.rehydrate(
                    ComplianceId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    ComplianceStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
