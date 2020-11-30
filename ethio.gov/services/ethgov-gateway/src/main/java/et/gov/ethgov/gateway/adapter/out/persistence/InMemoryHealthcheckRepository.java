package et.gov.ethgov.gateway.adapter.out.persistence;

import et.gov.ethgov.gateway.domain.healthcheck.Healthcheck;
import et.gov.ethgov.gateway.domain.healthcheck.HealthcheckId;
import et.gov.ethgov.gateway.domain.healthcheck.HealthcheckRepository;
import et.gov.ethgov.gateway.domain.healthcheck.HealthcheckStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Healthcheck (replaceable with JPA). */
public final class InMemoryHealthcheckRepository implements HealthcheckRepository {
    private final Map<UUID, StoredHealthcheck> store = new ConcurrentHashMap<>();

    @Override
    public Healthcheck save(Healthcheck aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredHealthcheck.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Healthcheck> findById(HealthcheckId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredHealthcheck::toDomain);
    }

    @Override
    public Optional<Healthcheck> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredHealthcheck::toDomain)
                .findFirst();
    }

    @Override
    public List<Healthcheck> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredHealthcheck row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredHealthcheck::toDomain)
                .toList();
    }

    @Override
    public void delete(HealthcheckId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredHealthcheck(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredHealthcheck from(Healthcheck aggregate) {
            return new StoredHealthcheck(
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

        Healthcheck toDomain() {
            return Healthcheck.rehydrate(
                    HealthcheckId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    HealthcheckStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
