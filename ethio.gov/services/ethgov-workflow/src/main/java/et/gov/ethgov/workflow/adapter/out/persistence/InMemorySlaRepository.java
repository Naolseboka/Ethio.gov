package et.gov.ethgov.workflow.adapter.out.persistence;

import et.gov.ethgov.workflow.domain.sla.Sla;
import et.gov.ethgov.workflow.domain.sla.SlaId;
import et.gov.ethgov.workflow.domain.sla.SlaRepository;
import et.gov.ethgov.workflow.domain.sla.SlaStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Sla (replaceable with JPA). */
public final class InMemorySlaRepository implements SlaRepository {
    private final Map<UUID, StoredSla> store = new ConcurrentHashMap<>();

    @Override
    public Sla save(Sla aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredSla.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Sla> findById(SlaId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredSla::toDomain);
    }

    @Override
    public Optional<Sla> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredSla::toDomain)
                .findFirst();
    }

    @Override
    public List<Sla> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredSla row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredSla::toDomain)
                .toList();
    }

    @Override
    public void delete(SlaId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredSla(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredSla from(Sla aggregate) {
            return new StoredSla(
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

        Sla toDomain() {
            return Sla.rehydrate(
                    SlaId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    SlaStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
