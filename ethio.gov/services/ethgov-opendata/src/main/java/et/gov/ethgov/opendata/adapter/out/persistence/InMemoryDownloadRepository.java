package et.gov.ethgov.opendata.adapter.out.persistence;

import et.gov.ethgov.opendata.domain.download.Download;
import et.gov.ethgov.opendata.domain.download.DownloadId;
import et.gov.ethgov.opendata.domain.download.DownloadRepository;
import et.gov.ethgov.opendata.domain.download.DownloadStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Download (replaceable with JPA). */
public final class InMemoryDownloadRepository implements DownloadRepository {
    private final Map<UUID, StoredDownload> store = new ConcurrentHashMap<>();

    @Override
    public Download save(Download aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredDownload.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Download> findById(DownloadId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredDownload::toDomain);
    }

    @Override
    public Optional<Download> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredDownload::toDomain)
                .findFirst();
    }

    @Override
    public List<Download> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredDownload row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredDownload::toDomain)
                .toList();
    }

    @Override
    public void delete(DownloadId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredDownload(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredDownload from(Download aggregate) {
            return new StoredDownload(
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

        Download toDomain() {
            return Download.rehydrate(
                    DownloadId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    DownloadStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
