package et.gov.ethgov.gateway.adapter.out.persistence;

import et.gov.ethgov.gateway.domain.rewrite.Rewrite;
import et.gov.ethgov.gateway.domain.rewrite.RewriteId;
import et.gov.ethgov.gateway.domain.rewrite.RewriteRepository;
import et.gov.ethgov.gateway.domain.rewrite.RewriteStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Rewrite (replaceable with JPA). */
public final class InMemoryRewriteRepository implements RewriteRepository {
    private final Map<UUID, StoredRewrite> store = new ConcurrentHashMap<>();

    @Override
    public Rewrite save(Rewrite aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredRewrite.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Rewrite> findById(RewriteId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredRewrite::toDomain);
    }

    @Override
    public Optional<Rewrite> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredRewrite::toDomain)
                .findFirst();
    }

    @Override
    public List<Rewrite> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredRewrite row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredRewrite::toDomain)
                .toList();
    }

    @Override
    public void delete(RewriteId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredRewrite(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredRewrite from(Rewrite aggregate) {
            return new StoredRewrite(
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

        Rewrite toDomain() {
            return Rewrite.rehydrate(
                    RewriteId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    RewriteStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
