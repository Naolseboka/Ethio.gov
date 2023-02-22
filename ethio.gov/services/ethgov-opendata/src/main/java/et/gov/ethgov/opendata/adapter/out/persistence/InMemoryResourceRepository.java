package et.gov.ethgov.opendata.adapter.out.persistence;

import et.gov.ethgov.opendata.domain.resource.Resource;
import et.gov.ethgov.opendata.domain.resource.ResourceId;
import et.gov.ethgov.opendata.domain.resource.ResourceRepository;
import et.gov.ethgov.opendata.domain.resource.ResourceStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Resource (replaceable with JPA). */
public final class InMemoryResourceRepository implements ResourceRepository {
    private final Map<UUID, StoredResource> store = new ConcurrentHashMap<>();

    @Override
    public Resource save(Resource aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredResource.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Resource> findById(ResourceId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredResource::toDomain);
    }

    @Override
    public Optional<Resource> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredResource::toDomain)
                .findFirst();
    }

    @Override
    public List<Resource> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredResource row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredResource::toDomain)
                .toList();
    }

    @Override
    public void delete(ResourceId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredResource(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredResource from(Resource aggregate) {
            return new StoredResource(
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

        Resource toDomain() {
            return Resource.rehydrate(
                    ResourceId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    ResourceStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
