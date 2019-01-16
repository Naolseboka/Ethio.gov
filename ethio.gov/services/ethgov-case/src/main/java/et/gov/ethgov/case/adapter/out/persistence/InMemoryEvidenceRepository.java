package et.gov.ethgov.case.adapter.out.persistence;

import et.gov.ethgov.case.domain.evidence.Evidence;
import et.gov.ethgov.case.domain.evidence.EvidenceId;
import et.gov.ethgov.case.domain.evidence.EvidenceRepository;
import et.gov.ethgov.case.domain.evidence.EvidenceStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Evidence (replaceable with JPA). */
public final class InMemoryEvidenceRepository implements EvidenceRepository {
    private final Map<UUID, StoredEvidence> store = new ConcurrentHashMap<>();

    @Override
    public Evidence save(Evidence aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredEvidence.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Evidence> findById(EvidenceId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredEvidence::toDomain);
    }

    @Override
    public Optional<Evidence> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredEvidence::toDomain)
                .findFirst();
    }

    @Override
    public List<Evidence> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredEvidence row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredEvidence::toDomain)
                .toList();
    }

    @Override
    public void delete(EvidenceId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredEvidence(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredEvidence from(Evidence aggregate) {
            return new StoredEvidence(
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

        Evidence toDomain() {
            return Evidence.rehydrate(
                    EvidenceId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    EvidenceStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
