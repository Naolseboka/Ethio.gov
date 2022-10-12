package et.gov.ethgov.notification.adapter.out.persistence;

import et.gov.ethgov.notification.domain.subscription.Subscription;
import et.gov.ethgov.notification.domain.subscription.SubscriptionId;
import et.gov.ethgov.notification.domain.subscription.SubscriptionRepository;
import et.gov.ethgov.notification.domain.subscription.SubscriptionStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Subscription (replaceable with JPA). */
public final class InMemorySubscriptionRepository implements SubscriptionRepository {
    private final Map<UUID, StoredSubscription> store = new ConcurrentHashMap<>();

    @Override
    public Subscription save(Subscription aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredSubscription.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Subscription> findById(SubscriptionId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredSubscription::toDomain);
    }

    @Override
    public Optional<Subscription> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredSubscription::toDomain)
                .findFirst();
    }

    @Override
    public List<Subscription> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredSubscription row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredSubscription::toDomain)
                .toList();
    }

    @Override
    public void delete(SubscriptionId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredSubscription(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredSubscription from(Subscription aggregate) {
            return new StoredSubscription(
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

        Subscription toDomain() {
            return Subscription.rehydrate(
                    SubscriptionId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    SubscriptionStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
