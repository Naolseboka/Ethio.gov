package et.gov.ethgov.analytics.adapter.out.persistence;

import et.gov.ethgov.analytics.domain.segment.Segment;
import et.gov.ethgov.analytics.domain.segment.SegmentId;
import et.gov.ethgov.analytics.domain.segment.SegmentRepository;
import et.gov.ethgov.analytics.domain.segment.SegmentStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Segment (replaceable with JPA). */
public final class InMemorySegmentRepository implements SegmentRepository {
    private final Map<UUID, StoredSegment> store = new ConcurrentHashMap<>();

    @Override
    public Segment save(Segment aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredSegment.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Segment> findById(SegmentId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredSegment::toDomain);
    }

    @Override
    public Optional<Segment> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredSegment::toDomain)
                .findFirst();
    }

    @Override
    public List<Segment> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredSegment row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredSegment::toDomain)
                .toList();
    }

    @Override
    public void delete(SegmentId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredSegment(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredSegment from(Segment aggregate) {
            return new StoredSegment(
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

        Segment toDomain() {
            return Segment.rehydrate(
                    SegmentId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    SegmentStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
