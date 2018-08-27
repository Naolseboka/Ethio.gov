package et.gov.ethgov.analytics.adapter.out.persistence;

import et.gov.ethgov.analytics.domain.cube.Cube;
import et.gov.ethgov.analytics.domain.cube.CubeId;
import et.gov.ethgov.analytics.domain.cube.CubeRepository;
import et.gov.ethgov.analytics.domain.cube.CubeStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Cube (replaceable with JPA). */
public final class InMemoryCubeRepository implements CubeRepository {
    private final Map<UUID, StoredCube> store = new ConcurrentHashMap<>();

    @Override
    public Cube save(Cube aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredCube.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Cube> findById(CubeId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredCube::toDomain);
    }

    @Override
    public Optional<Cube> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredCube::toDomain)
                .findFirst();
    }

    @Override
    public List<Cube> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredCube row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredCube::toDomain)
                .toList();
    }

    @Override
    public void delete(CubeId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredCube(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredCube from(Cube aggregate) {
            return new StoredCube(
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

        Cube toDomain() {
            return Cube.rehydrate(
                    CubeId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    CubeStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
