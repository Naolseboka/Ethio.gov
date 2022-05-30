package et.gov.ethgov.licensing.adapter.out.persistence;

import et.gov.ethgov.licensing.domain.revocation.Revocation;
import et.gov.ethgov.licensing.domain.revocation.RevocationId;
import et.gov.ethgov.licensing.domain.revocation.RevocationRepository;
import et.gov.ethgov.licensing.domain.revocation.RevocationStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Revocation (replaceable with JPA). */
public final class InMemoryRevocationRepository implements RevocationRepository {
    private final Map<UUID, StoredRevocation> store = new ConcurrentHashMap<>();

    @Override
    public Revocation save(Revocation aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredRevocation.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Revocation> findById(RevocationId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredRevocation::toDomain);
    }

    @Override
    public Optional<Revocation> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredRevocation::toDomain)
                .findFirst();
    }

    @Override
    public List<Revocation> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredRevocation row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredRevocation::toDomain)
                .toList();
    }

    @Override
    public void delete(RevocationId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredRevocation(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredRevocation from(Revocation aggregate) {
            return new StoredRevocation(
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

        Revocation toDomain() {
            return Revocation.rehydrate(
                    RevocationId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    RevocationStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
