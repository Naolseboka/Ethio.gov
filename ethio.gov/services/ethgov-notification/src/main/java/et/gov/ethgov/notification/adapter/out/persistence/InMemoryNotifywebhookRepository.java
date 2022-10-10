package et.gov.ethgov.notification.adapter.out.persistence;

import et.gov.ethgov.notification.domain.notifywebhook.Notifywebhook;
import et.gov.ethgov.notification.domain.notifywebhook.NotifywebhookId;
import et.gov.ethgov.notification.domain.notifywebhook.NotifywebhookRepository;
import et.gov.ethgov.notification.domain.notifywebhook.NotifywebhookStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Notifywebhook (replaceable with JPA). */
public final class InMemoryNotifywebhookRepository implements NotifywebhookRepository {
    private final Map<UUID, StoredNotifywebhook> store = new ConcurrentHashMap<>();

    @Override
    public Notifywebhook save(Notifywebhook aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredNotifywebhook.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Notifywebhook> findById(NotifywebhookId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredNotifywebhook::toDomain);
    }

    @Override
    public Optional<Notifywebhook> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredNotifywebhook::toDomain)
                .findFirst();
    }

    @Override
    public List<Notifywebhook> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredNotifywebhook row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredNotifywebhook::toDomain)
                .toList();
    }

    @Override
    public void delete(NotifywebhookId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredNotifywebhook(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredNotifywebhook from(Notifywebhook aggregate) {
            return new StoredNotifywebhook(
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

        Notifywebhook toDomain() {
            return Notifywebhook.rehydrate(
                    NotifywebhookId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    NotifywebhookStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
