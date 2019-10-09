package et.gov.ethgov.developer.adapter.out.persistence;

import et.gov.ethgov.developer.domain.devwebhook.Devwebhook;
import et.gov.ethgov.developer.domain.devwebhook.DevwebhookId;
import et.gov.ethgov.developer.domain.devwebhook.DevwebhookRepository;
import et.gov.ethgov.developer.domain.devwebhook.DevwebhookStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Devwebhook (replaceable with JPA). */
public final class InMemoryDevwebhookRepository implements DevwebhookRepository {
    private final Map<UUID, StoredDevwebhook> store = new ConcurrentHashMap<>();

    @Override
    public Devwebhook save(Devwebhook aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredDevwebhook.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Devwebhook> findById(DevwebhookId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredDevwebhook::toDomain);
    }

    @Override
    public Optional<Devwebhook> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredDevwebhook::toDomain)
                .findFirst();
    }

    @Override
    public List<Devwebhook> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredDevwebhook row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredDevwebhook::toDomain)
                .toList();
    }

    @Override
    public void delete(DevwebhookId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredDevwebhook(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredDevwebhook from(Devwebhook aggregate) {
            return new StoredDevwebhook(
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

        Devwebhook toDomain() {
            return Devwebhook.rehydrate(
                    DevwebhookId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    DevwebhookStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
