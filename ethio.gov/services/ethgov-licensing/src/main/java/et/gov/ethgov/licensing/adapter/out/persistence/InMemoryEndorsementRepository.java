package et.gov.ethgov.licensing.adapter.out.persistence;

import et.gov.ethgov.licensing.domain.endorsement.Endorsement;
import et.gov.ethgov.licensing.domain.endorsement.EndorsementId;
import et.gov.ethgov.licensing.domain.endorsement.EndorsementRepository;
import et.gov.ethgov.licensing.domain.endorsement.EndorsementStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Endorsement (replaceable with JPA). */
public final class InMemoryEndorsementRepository implements EndorsementRepository {
    private final Map<UUID, StoredEndorsement> store = new ConcurrentHashMap<>();

    @Override
    public Endorsement save(Endorsement aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredEndorsement.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Endorsement> findById(EndorsementId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredEndorsement::toDomain);
    }

    @Override
    public Optional<Endorsement> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredEndorsement::toDomain)
                .findFirst();
    }

    @Override
    public List<Endorsement> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredEndorsement row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredEndorsement::toDomain)
                .toList();
    }

    @Override
    public void delete(EndorsementId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredEndorsement(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredEndorsement from(Endorsement aggregate) {
            return new StoredEndorsement(
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

        Endorsement toDomain() {
            return Endorsement.rehydrate(
                    EndorsementId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    EndorsementStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
