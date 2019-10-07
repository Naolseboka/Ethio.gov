package et.gov.ethgov.developer.adapter.out.persistence;

import et.gov.ethgov.developer.domain.apiproduct.Apiproduct;
import et.gov.ethgov.developer.domain.apiproduct.ApiproductId;
import et.gov.ethgov.developer.domain.apiproduct.ApiproductRepository;
import et.gov.ethgov.developer.domain.apiproduct.ApiproductStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Apiproduct (replaceable with JPA). */
public final class InMemoryApiproductRepository implements ApiproductRepository {
    private final Map<UUID, StoredApiproduct> store = new ConcurrentHashMap<>();

    @Override
    public Apiproduct save(Apiproduct aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredApiproduct.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Apiproduct> findById(ApiproductId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredApiproduct::toDomain);
    }

    @Override
    public Optional<Apiproduct> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredApiproduct::toDomain)
                .findFirst();
    }

    @Override
    public List<Apiproduct> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredApiproduct row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredApiproduct::toDomain)
                .toList();
    }

    @Override
    public void delete(ApiproductId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredApiproduct(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredApiproduct from(Apiproduct aggregate) {
            return new StoredApiproduct(
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

        Apiproduct toDomain() {
            return Apiproduct.rehydrate(
                    ApiproductId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    ApiproductStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
