package et.gov.ethgov.gateway.adapter.out.persistence;

import et.gov.ethgov.gateway.domain.plugin.Plugin;
import et.gov.ethgov.gateway.domain.plugin.PluginId;
import et.gov.ethgov.gateway.domain.plugin.PluginRepository;
import et.gov.ethgov.gateway.domain.plugin.PluginStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Plugin (replaceable with JPA). */
public final class InMemoryPluginRepository implements PluginRepository {
    private final Map<UUID, StoredPlugin> store = new ConcurrentHashMap<>();

    @Override
    public Plugin save(Plugin aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredPlugin.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Plugin> findById(PluginId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredPlugin::toDomain);
    }

    @Override
    public Optional<Plugin> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredPlugin::toDomain)
                .findFirst();
    }

    @Override
    public List<Plugin> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredPlugin row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredPlugin::toDomain)
                .toList();
    }

    @Override
    public void delete(PluginId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredPlugin(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredPlugin from(Plugin aggregate) {
            return new StoredPlugin(
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

        Plugin toDomain() {
            return Plugin.rehydrate(
                    PluginId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    PluginStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
