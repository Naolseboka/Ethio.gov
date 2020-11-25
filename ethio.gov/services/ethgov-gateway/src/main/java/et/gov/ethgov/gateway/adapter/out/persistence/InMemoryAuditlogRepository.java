package et.gov.ethgov.gateway.adapter.out.persistence;

import et.gov.ethgov.gateway.domain.auditlog.Auditlog;
import et.gov.ethgov.gateway.domain.auditlog.AuditlogId;
import et.gov.ethgov.gateway.domain.auditlog.AuditlogRepository;
import et.gov.ethgov.gateway.domain.auditlog.AuditlogStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Auditlog (replaceable with JPA). */
public final class InMemoryAuditlogRepository implements AuditlogRepository {
    private final Map<UUID, StoredAuditlog> store = new ConcurrentHashMap<>();

    @Override
    public Auditlog save(Auditlog aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredAuditlog.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Auditlog> findById(AuditlogId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredAuditlog::toDomain);
    }

    @Override
    public Optional<Auditlog> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredAuditlog::toDomain)
                .findFirst();
    }

    @Override
    public List<Auditlog> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredAuditlog row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredAuditlog::toDomain)
                .toList();
    }

    @Override
    public void delete(AuditlogId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredAuditlog(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredAuditlog from(Auditlog aggregate) {
            return new StoredAuditlog(
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

        Auditlog toDomain() {
            return Auditlog.rehydrate(
                    AuditlogId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    AuditlogStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
