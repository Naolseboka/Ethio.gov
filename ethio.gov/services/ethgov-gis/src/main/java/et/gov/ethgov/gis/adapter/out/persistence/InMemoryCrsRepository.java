package et.gov.ethgov.gis.adapter.out.persistence;

import et.gov.ethgov.gis.domain.crs.Crs;
import et.gov.ethgov.gis.domain.crs.CrsId;
import et.gov.ethgov.gis.domain.crs.CrsRepository;
import et.gov.ethgov.gis.domain.crs.CrsStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Crs (replaceable with JPA). */
public final class InMemoryCrsRepository implements CrsRepository {
    private final Map<UUID, StoredCrs> store = new ConcurrentHashMap<>();

    @Override
    public Crs save(Crs aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredCrs.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Crs> findById(CrsId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredCrs::toDomain);
    }

    @Override
    public Optional<Crs> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredCrs::toDomain)
                .findFirst();
    }

    @Override
    public List<Crs> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredCrs row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredCrs::toDomain)
                .toList();
    }

    @Override
    public void delete(CrsId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredCrs(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredCrs from(Crs aggregate) {
            return new StoredCrs(
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

        Crs toDomain() {
            return Crs.rehydrate(
                    CrsId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    CrsStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
