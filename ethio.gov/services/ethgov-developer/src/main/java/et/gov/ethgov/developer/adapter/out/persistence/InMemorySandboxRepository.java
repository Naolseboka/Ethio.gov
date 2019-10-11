package et.gov.ethgov.developer.adapter.out.persistence;

import et.gov.ethgov.developer.domain.sandbox.Sandbox;
import et.gov.ethgov.developer.domain.sandbox.SandboxId;
import et.gov.ethgov.developer.domain.sandbox.SandboxRepository;
import et.gov.ethgov.developer.domain.sandbox.SandboxStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Sandbox (replaceable with JPA). */
public final class InMemorySandboxRepository implements SandboxRepository {
    private final Map<UUID, StoredSandbox> store = new ConcurrentHashMap<>();

    @Override
    public Sandbox save(Sandbox aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredSandbox.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Sandbox> findById(SandboxId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredSandbox::toDomain);
    }

    @Override
    public Optional<Sandbox> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredSandbox::toDomain)
                .findFirst();
    }

    @Override
    public List<Sandbox> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredSandbox row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredSandbox::toDomain)
                .toList();
    }

    @Override
    public void delete(SandboxId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredSandbox(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredSandbox from(Sandbox aggregate) {
            return new StoredSandbox(
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

        Sandbox toDomain() {
            return Sandbox.rehydrate(
                    SandboxId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    SandboxStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
