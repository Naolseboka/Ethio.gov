package et.gov.ethgov.gateway.adapter.out.persistence;

import et.gov.ethgov.gateway.domain.circuitbreaker.Circuitbreaker;
import et.gov.ethgov.gateway.domain.circuitbreaker.CircuitbreakerId;
import et.gov.ethgov.gateway.domain.circuitbreaker.CircuitbreakerRepository;
import et.gov.ethgov.gateway.domain.circuitbreaker.CircuitbreakerStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Circuitbreaker (replaceable with JPA). */
public final class InMemoryCircuitbreakerRepository implements CircuitbreakerRepository {
    private final Map<UUID, StoredCircuitbreaker> store = new ConcurrentHashMap<>();

    @Override
    public Circuitbreaker save(Circuitbreaker aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredCircuitbreaker.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Circuitbreaker> findById(CircuitbreakerId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredCircuitbreaker::toDomain);
    }

    @Override
    public Optional<Circuitbreaker> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredCircuitbreaker::toDomain)
                .findFirst();
    }

    @Override
    public List<Circuitbreaker> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredCircuitbreaker row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredCircuitbreaker::toDomain)
                .toList();
    }

    @Override
    public void delete(CircuitbreakerId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredCircuitbreaker(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredCircuitbreaker from(Circuitbreaker aggregate) {
            return new StoredCircuitbreaker(
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

        Circuitbreaker toDomain() {
            return Circuitbreaker.rehydrate(
                    CircuitbreakerId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    CircuitbreakerStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
