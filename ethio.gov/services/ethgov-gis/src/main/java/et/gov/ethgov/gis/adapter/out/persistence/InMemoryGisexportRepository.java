package et.gov.ethgov.gis.adapter.out.persistence;

import et.gov.ethgov.gis.domain.gisexport.Gisexport;
import et.gov.ethgov.gis.domain.gisexport.GisexportId;
import et.gov.ethgov.gis.domain.gisexport.GisexportRepository;
import et.gov.ethgov.gis.domain.gisexport.GisexportStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Gisexport (replaceable with JPA). */
public final class InMemoryGisexportRepository implements GisexportRepository {
    private final Map<UUID, StoredGisexport> store = new ConcurrentHashMap<>();

    @Override
    public Gisexport save(Gisexport aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredGisexport.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Gisexport> findById(GisexportId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredGisexport::toDomain);
    }

    @Override
    public Optional<Gisexport> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredGisexport::toDomain)
                .findFirst();
    }

    @Override
    public List<Gisexport> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredGisexport row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredGisexport::toDomain)
                .toList();
    }

    @Override
    public void delete(GisexportId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredGisexport(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredGisexport from(Gisexport aggregate) {
            return new StoredGisexport(
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

        Gisexport toDomain() {
            return Gisexport.rehydrate(
                    GisexportId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    GisexportStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
