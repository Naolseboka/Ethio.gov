package et.gov.ethgov.notification.adapter.out.persistence;

import et.gov.ethgov.notification.domain.notifychannel.Notifychannel;
import et.gov.ethgov.notification.domain.notifychannel.NotifychannelId;
import et.gov.ethgov.notification.domain.notifychannel.NotifychannelRepository;
import et.gov.ethgov.notification.domain.notifychannel.NotifychannelStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Notifychannel (replaceable with JPA). */
public final class InMemoryNotifychannelRepository implements NotifychannelRepository {
    private final Map<UUID, StoredNotifychannel> store = new ConcurrentHashMap<>();

    @Override
    public Notifychannel save(Notifychannel aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredNotifychannel.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Notifychannel> findById(NotifychannelId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredNotifychannel::toDomain);
    }

    @Override
    public Optional<Notifychannel> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredNotifychannel::toDomain)
                .findFirst();
    }

    @Override
    public List<Notifychannel> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredNotifychannel row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredNotifychannel::toDomain)
                .toList();
    }

    @Override
    public void delete(NotifychannelId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredNotifychannel(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredNotifychannel from(Notifychannel aggregate) {
            return new StoredNotifychannel(
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

        Notifychannel toDomain() {
            return Notifychannel.rehydrate(
                    NotifychannelId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    NotifychannelStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
