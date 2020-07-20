package et.gov.ethgov.document.adapter.out.persistence;

import et.gov.ethgov.document.domain.versioning.Versioning;
import et.gov.ethgov.document.domain.versioning.VersioningId;
import et.gov.ethgov.document.domain.versioning.VersioningRepository;
import et.gov.ethgov.document.domain.versioning.VersioningStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Versioning (replaceable with JPA). */
public final class InMemoryVersioningRepository implements VersioningRepository {
    private final Map<UUID, StoredVersioning> store = new ConcurrentHashMap<>();

    @Override
    public Versioning save(Versioning aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredVersioning.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Versioning> findById(VersioningId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredVersioning::toDomain);
    }

    @Override
    public Optional<Versioning> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredVersioning::toDomain)
                .findFirst();
    }

    @Override
    public List<Versioning> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredVersioning row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredVersioning::toDomain)
                .toList();
    }

    @Override
    public void delete(VersioningId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredVersioning(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredVersioning from(Versioning aggregate) {
            return new StoredVersioning(
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

        Versioning toDomain() {
            return Versioning.rehydrate(
                    VersioningId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    VersioningStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
