package et.gov.ethgov.directory.adapter.out.persistence;

import et.gov.ethgov.directory.domain.directoryentry.Directoryentry;
import et.gov.ethgov.directory.domain.directoryentry.DirectoryentryId;
import et.gov.ethgov.directory.domain.directoryentry.DirectoryentryRepository;
import et.gov.ethgov.directory.domain.directoryentry.DirectoryentryStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Directoryentry (replaceable with JPA). */
public final class InMemoryDirectoryentryRepository implements DirectoryentryRepository {
    private final Map<UUID, StoredDirectoryentry> store = new ConcurrentHashMap<>();

    @Override
    public Directoryentry save(Directoryentry aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredDirectoryentry.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Directoryentry> findById(DirectoryentryId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredDirectoryentry::toDomain);
    }

    @Override
    public Optional<Directoryentry> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredDirectoryentry::toDomain)
                .findFirst();
    }

    @Override
    public List<Directoryentry> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredDirectoryentry row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredDirectoryentry::toDomain)
                .toList();
    }

    @Override
    public void delete(DirectoryentryId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredDirectoryentry(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredDirectoryentry from(Directoryentry aggregate) {
            return new StoredDirectoryentry(
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

        Directoryentry toDomain() {
            return Directoryentry.rehydrate(
                    DirectoryentryId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    DirectoryentryStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
