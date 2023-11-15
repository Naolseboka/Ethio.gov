package et.gov.ethgov.payments.adapter.out.persistence;

import et.gov.ethgov.payments.domain.channel.Channel;
import et.gov.ethgov.payments.domain.channel.ChannelId;
import et.gov.ethgov.payments.domain.channel.ChannelRepository;
import et.gov.ethgov.payments.domain.channel.ChannelStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Channel (replaceable with JPA). */
public final class InMemoryChannelRepository implements ChannelRepository {
    private final Map<UUID, StoredChannel> store = new ConcurrentHashMap<>();

    @Override
    public Channel save(Channel aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredChannel.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Channel> findById(ChannelId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredChannel::toDomain);
    }

    @Override
    public Optional<Channel> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredChannel::toDomain)
                .findFirst();
    }

    @Override
    public List<Channel> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredChannel row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredChannel::toDomain)
                .toList();
    }

    @Override
    public void delete(ChannelId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredChannel(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredChannel from(Channel aggregate) {
            return new StoredChannel(
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

        Channel toDomain() {
            return Channel.rehydrate(
                    ChannelId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    ChannelStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
