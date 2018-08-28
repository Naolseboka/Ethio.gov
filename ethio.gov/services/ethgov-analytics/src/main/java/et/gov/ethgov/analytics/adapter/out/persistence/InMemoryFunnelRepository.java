package et.gov.ethgov.analytics.adapter.out.persistence;

import et.gov.ethgov.analytics.domain.funnel.Funnel;
import et.gov.ethgov.analytics.domain.funnel.FunnelId;
import et.gov.ethgov.analytics.domain.funnel.FunnelRepository;
import et.gov.ethgov.analytics.domain.funnel.FunnelStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Funnel (replaceable with JPA). */
public final class InMemoryFunnelRepository implements FunnelRepository {
    private final Map<UUID, StoredFunnel> store = new ConcurrentHashMap<>();

    @Override
    public Funnel save(Funnel aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredFunnel.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Funnel> findById(FunnelId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredFunnel::toDomain);
    }

    @Override
    public Optional<Funnel> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredFunnel::toDomain)
                .findFirst();
    }

    @Override
    public List<Funnel> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredFunnel row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredFunnel::toDomain)
                .toList();
    }

    @Override
    public void delete(FunnelId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredFunnel(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredFunnel from(Funnel aggregate) {
            return new StoredFunnel(
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

        Funnel toDomain() {
            return Funnel.rehydrate(
                    FunnelId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    FunnelStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
