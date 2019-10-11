package et.gov.ethgov.developer.adapter.out.persistence;

import et.gov.ethgov.developer.domain.quota.Quota;
import et.gov.ethgov.developer.domain.quota.QuotaId;
import et.gov.ethgov.developer.domain.quota.QuotaRepository;
import et.gov.ethgov.developer.domain.quota.QuotaStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Quota (replaceable with JPA). */
public final class InMemoryQuotaRepository implements QuotaRepository {
    private final Map<UUID, StoredQuota> store = new ConcurrentHashMap<>();

    @Override
    public Quota save(Quota aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredQuota.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Quota> findById(QuotaId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredQuota::toDomain);
    }

    @Override
    public Optional<Quota> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredQuota::toDomain)
                .findFirst();
    }

    @Override
    public List<Quota> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredQuota row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredQuota::toDomain)
                .toList();
    }

    @Override
    public void delete(QuotaId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredQuota(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredQuota from(Quota aggregate) {
            return new StoredQuota(
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

        Quota toDomain() {
            return Quota.rehydrate(
                    QuotaId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    QuotaStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
