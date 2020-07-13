package et.gov.ethgov.document.adapter.out.persistence;

import et.gov.ethgov.document.domain.archivebox.Archivebox;
import et.gov.ethgov.document.domain.archivebox.ArchiveboxId;
import et.gov.ethgov.document.domain.archivebox.ArchiveboxRepository;
import et.gov.ethgov.document.domain.archivebox.ArchiveboxStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Archivebox (replaceable with JPA). */
public final class InMemoryArchiveboxRepository implements ArchiveboxRepository {
    private final Map<UUID, StoredArchivebox> store = new ConcurrentHashMap<>();

    @Override
    public Archivebox save(Archivebox aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredArchivebox.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Archivebox> findById(ArchiveboxId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredArchivebox::toDomain);
    }

    @Override
    public Optional<Archivebox> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredArchivebox::toDomain)
                .findFirst();
    }

    @Override
    public List<Archivebox> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredArchivebox row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredArchivebox::toDomain)
                .toList();
    }

    @Override
    public void delete(ArchiveboxId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredArchivebox(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredArchivebox from(Archivebox aggregate) {
            return new StoredArchivebox(
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

        Archivebox toDomain() {
            return Archivebox.rehydrate(
                    ArchiveboxId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    ArchiveboxStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
