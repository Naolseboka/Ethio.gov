package et.gov.ethgov.organization.adapter.out.persistence;

import et.gov.ethgov.organization.domain.budgetcode.Budgetcode;
import et.gov.ethgov.organization.domain.budgetcode.BudgetcodeId;
import et.gov.ethgov.organization.domain.budgetcode.BudgetcodeRepository;
import et.gov.ethgov.organization.domain.budgetcode.BudgetcodeStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Budgetcode (replaceable with JPA). */
public final class InMemoryBudgetcodeRepository implements BudgetcodeRepository {
    private final Map<UUID, StoredBudgetcode> store = new ConcurrentHashMap<>();

    @Override
    public Budgetcode save(Budgetcode aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredBudgetcode.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Budgetcode> findById(BudgetcodeId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredBudgetcode::toDomain);
    }

    @Override
    public Optional<Budgetcode> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredBudgetcode::toDomain)
                .findFirst();
    }

    @Override
    public List<Budgetcode> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredBudgetcode row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredBudgetcode::toDomain)
                .toList();
    }

    @Override
    public void delete(BudgetcodeId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredBudgetcode(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredBudgetcode from(Budgetcode aggregate) {
            return new StoredBudgetcode(
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

        Budgetcode toDomain() {
            return Budgetcode.rehydrate(
                    BudgetcodeId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    BudgetcodeStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
