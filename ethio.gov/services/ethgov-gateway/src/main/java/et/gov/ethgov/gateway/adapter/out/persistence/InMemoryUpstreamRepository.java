package et.gov.ethgov.gateway.adapter.out.persistence;

import et.gov.ethgov.gateway.domain.upstream.Upstream;
import et.gov.ethgov.gateway.domain.upstream.UpstreamId;
import et.gov.ethgov.gateway.domain.upstream.UpstreamRepository;
import et.gov.ethgov.gateway.domain.upstream.UpstreamStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Upstream (replaceable with JPA). */
public final class InMemoryUpstreamRepository implements UpstreamRepository {
    private final Map<UUID, StoredUpstream> store = new ConcurrentHashMap<>();

    @Override
    public Upstream save(Upstream aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredUpstream.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Upstream> findById(UpstreamId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredUpstream::toDomain);
    }

    @Override
    public Optional<Upstream> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredUpstream::toDomain)
                .findFirst();
    }

    @Override
    public List<Upstream> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredUpstream row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredUpstream::toDomain)
                .toList();
    }

    @Override
    public void delete(UpstreamId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredUpstream(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredUpstream from(Upstream aggregate) {
            return new StoredUpstream(
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

        Upstream toDomain() {
            return Upstream.rehydrate(
                    UpstreamId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    UpstreamStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
