package et.gov.ethgov.document.adapter.out.persistence;

import et.gov.ethgov.document.domain.ocrjob.Ocrjob;
import et.gov.ethgov.document.domain.ocrjob.OcrjobId;
import et.gov.ethgov.document.domain.ocrjob.OcrjobRepository;
import et.gov.ethgov.document.domain.ocrjob.OcrjobStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Ocrjob (replaceable with JPA). */
public final class InMemoryOcrjobRepository implements OcrjobRepository {
    private final Map<UUID, StoredOcrjob> store = new ConcurrentHashMap<>();

    @Override
    public Ocrjob save(Ocrjob aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredOcrjob.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Ocrjob> findById(OcrjobId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredOcrjob::toDomain);
    }

    @Override
    public Optional<Ocrjob> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredOcrjob::toDomain)
                .findFirst();
    }

    @Override
    public List<Ocrjob> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredOcrjob row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredOcrjob::toDomain)
                .toList();
    }

    @Override
    public void delete(OcrjobId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredOcrjob(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredOcrjob from(Ocrjob aggregate) {
            return new StoredOcrjob(
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

        Ocrjob toDomain() {
            return Ocrjob.rehydrate(
                    OcrjobId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    OcrjobStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
