package et.gov.ethgov.developer.adapter.out.persistence;

import et.gov.ethgov.developer.domain.usage.Usage;
import et.gov.ethgov.developer.domain.usage.UsageId;
import et.gov.ethgov.developer.domain.usage.UsageRepository;
import et.gov.ethgov.developer.domain.usage.UsageStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Usage (replaceable with JPA). */
public final class InMemoryUsageRepository implements UsageRepository {
    private final Map<UUID, StoredUsage> store = new ConcurrentHashMap<>();

    @Override
    public Usage save(Usage aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredUsage.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Usage> findById(UsageId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredUsage::toDomain);
    }

    @Override
    public Optional<Usage> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredUsage::toDomain)
                .findFirst();
    }

    @Override
    public List<Usage> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredUsage row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredUsage::toDomain)
                .toList();
    }

    @Override
    public void delete(UsageId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredUsage(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredUsage from(Usage aggregate) {
            return new StoredUsage(
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

        Usage toDomain() {
            return Usage.rehydrate(
                    UsageId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    UsageStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
