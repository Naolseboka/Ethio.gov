package et.gov.ethgov.permits.adapter.out.persistence;

import et.gov.ethgov.permits.domain.amendment.Amendment;
import et.gov.ethgov.permits.domain.amendment.AmendmentId;
import et.gov.ethgov.permits.domain.amendment.AmendmentRepository;
import et.gov.ethgov.permits.domain.amendment.AmendmentStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Amendment (replaceable with JPA). */
public final class InMemoryAmendmentRepository implements AmendmentRepository {
    private final Map<UUID, StoredAmendment> store = new ConcurrentHashMap<>();

    @Override
    public Amendment save(Amendment aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredAmendment.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Amendment> findById(AmendmentId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredAmendment::toDomain);
    }

    @Override
    public Optional<Amendment> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredAmendment::toDomain)
                .findFirst();
    }

    @Override
    public List<Amendment> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredAmendment row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredAmendment::toDomain)
                .toList();
    }

    @Override
    public void delete(AmendmentId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredAmendment(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredAmendment from(Amendment aggregate) {
            return new StoredAmendment(
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

        Amendment toDomain() {
            return Amendment.rehydrate(
                    AmendmentId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    AmendmentStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
