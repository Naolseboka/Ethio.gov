package et.gov.ethgov.opendata.adapter.out.persistence;

import et.gov.ethgov.opendata.domain.quality.Quality;
import et.gov.ethgov.opendata.domain.quality.QualityId;
import et.gov.ethgov.opendata.domain.quality.QualityRepository;
import et.gov.ethgov.opendata.domain.quality.QualityStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Quality (replaceable with JPA). */
public final class InMemoryQualityRepository implements QualityRepository {
    private final Map<UUID, StoredQuality> store = new ConcurrentHashMap<>();

    @Override
    public Quality save(Quality aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredQuality.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Quality> findById(QualityId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredQuality::toDomain);
    }

    @Override
    public Optional<Quality> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredQuality::toDomain)
                .findFirst();
    }

    @Override
    public List<Quality> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredQuality row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredQuality::toDomain)
                .toList();
    }

    @Override
    public void delete(QualityId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredQuality(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredQuality from(Quality aggregate) {
            return new StoredQuality(
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

        Quality toDomain() {
            return Quality.rehydrate(
                    QualityId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    QualityStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
