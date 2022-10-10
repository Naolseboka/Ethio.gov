package et.gov.ethgov.notification.adapter.out.persistence;

import et.gov.ethgov.notification.domain.digest.Digest;
import et.gov.ethgov.notification.domain.digest.DigestId;
import et.gov.ethgov.notification.domain.digest.DigestRepository;
import et.gov.ethgov.notification.domain.digest.DigestStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Digest (replaceable with JPA). */
public final class InMemoryDigestRepository implements DigestRepository {
    private final Map<UUID, StoredDigest> store = new ConcurrentHashMap<>();

    @Override
    public Digest save(Digest aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredDigest.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Digest> findById(DigestId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredDigest::toDomain);
    }

    @Override
    public Optional<Digest> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredDigest::toDomain)
                .findFirst();
    }

    @Override
    public List<Digest> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredDigest row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredDigest::toDomain)
                .toList();
    }

    @Override
    public void delete(DigestId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredDigest(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredDigest from(Digest aggregate) {
            return new StoredDigest(
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

        Digest toDomain() {
            return Digest.rehydrate(
                    DigestId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    DigestStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
