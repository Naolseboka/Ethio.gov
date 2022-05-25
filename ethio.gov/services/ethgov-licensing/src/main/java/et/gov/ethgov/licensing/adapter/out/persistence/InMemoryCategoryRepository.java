package et.gov.ethgov.licensing.adapter.out.persistence;

import et.gov.ethgov.licensing.domain.category.Category;
import et.gov.ethgov.licensing.domain.category.CategoryId;
import et.gov.ethgov.licensing.domain.category.CategoryRepository;
import et.gov.ethgov.licensing.domain.category.CategoryStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Category (replaceable with JPA). */
public final class InMemoryCategoryRepository implements CategoryRepository {
    private final Map<UUID, StoredCategory> store = new ConcurrentHashMap<>();

    @Override
    public Category save(Category aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredCategory.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Category> findById(CategoryId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredCategory::toDomain);
    }

    @Override
    public Optional<Category> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredCategory::toDomain)
                .findFirst();
    }

    @Override
    public List<Category> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredCategory row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredCategory::toDomain)
                .toList();
    }

    @Override
    public void delete(CategoryId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredCategory(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredCategory from(Category aggregate) {
            return new StoredCategory(
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

        Category toDomain() {
            return Category.rehydrate(
                    CategoryId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    CategoryStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
