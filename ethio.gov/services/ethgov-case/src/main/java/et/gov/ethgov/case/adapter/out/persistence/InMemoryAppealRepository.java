package et.gov.ethgov.case.adapter.out.persistence;

import et.gov.ethgov.case.domain.appeal.Appeal;
import et.gov.ethgov.case.domain.appeal.AppealId;
import et.gov.ethgov.case.domain.appeal.AppealRepository;
import et.gov.ethgov.case.domain.appeal.AppealStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Appeal (replaceable with JPA). */
public final class InMemoryAppealRepository implements AppealRepository {
    private final Map<UUID, StoredAppeal> store = new ConcurrentHashMap<>();

    @Override
    public Appeal save(Appeal aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredAppeal.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Appeal> findById(AppealId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredAppeal::toDomain);
    }

    @Override
    public Optional<Appeal> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredAppeal::toDomain)
                .findFirst();
    }

    @Override
    public List<Appeal> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredAppeal row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredAppeal::toDomain)
                .toList();
    }

    @Override
    public void delete(AppealId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredAppeal(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredAppeal from(Appeal aggregate) {
            return new StoredAppeal(
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

        Appeal toDomain() {
            return Appeal.rehydrate(
                    AppealId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    AppealStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
