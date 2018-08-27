package et.gov.ethgov.analytics.adapter.out.persistence;

import et.gov.ethgov.analytics.domain.attribution.Attribution;
import et.gov.ethgov.analytics.domain.attribution.AttributionId;
import et.gov.ethgov.analytics.domain.attribution.AttributionRepository;
import et.gov.ethgov.analytics.domain.attribution.AttributionStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Attribution (replaceable with JPA). */
public final class InMemoryAttributionRepository implements AttributionRepository {
    private final Map<UUID, StoredAttribution> store = new ConcurrentHashMap<>();

    @Override
    public Attribution save(Attribution aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredAttribution.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Attribution> findById(AttributionId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredAttribution::toDomain);
    }

    @Override
    public Optional<Attribution> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredAttribution::toDomain)
                .findFirst();
    }

    @Override
    public List<Attribution> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredAttribution row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredAttribution::toDomain)
                .toList();
    }

    @Override
    public void delete(AttributionId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredAttribution(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredAttribution from(Attribution aggregate) {
            return new StoredAttribution(
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

        Attribution toDomain() {
            return Attribution.rehydrate(
                    AttributionId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    AttributionStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
