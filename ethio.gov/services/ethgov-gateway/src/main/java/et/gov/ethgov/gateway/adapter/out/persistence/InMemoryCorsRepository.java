package et.gov.ethgov.gateway.adapter.out.persistence;

import et.gov.ethgov.gateway.domain.cors.Cors;
import et.gov.ethgov.gateway.domain.cors.CorsId;
import et.gov.ethgov.gateway.domain.cors.CorsRepository;
import et.gov.ethgov.gateway.domain.cors.CorsStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Cors (replaceable with JPA). */
public final class InMemoryCorsRepository implements CorsRepository {
    private final Map<UUID, StoredCors> store = new ConcurrentHashMap<>();

    @Override
    public Cors save(Cors aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredCors.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Cors> findById(CorsId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredCors::toDomain);
    }

    @Override
    public Optional<Cors> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredCors::toDomain)
                .findFirst();
    }

    @Override
    public List<Cors> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredCors row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredCors::toDomain)
                .toList();
    }

    @Override
    public void delete(CorsId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredCors(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredCors from(Cors aggregate) {
            return new StoredCors(
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

        Cors toDomain() {
            return Cors.rehydrate(
                    CorsId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    CorsStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
