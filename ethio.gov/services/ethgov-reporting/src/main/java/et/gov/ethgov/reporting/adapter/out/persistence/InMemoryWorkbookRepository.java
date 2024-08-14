package et.gov.ethgov.reporting.adapter.out.persistence;

import et.gov.ethgov.reporting.domain.workbook.Workbook;
import et.gov.ethgov.reporting.domain.workbook.WorkbookId;
import et.gov.ethgov.reporting.domain.workbook.WorkbookRepository;
import et.gov.ethgov.reporting.domain.workbook.WorkbookStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Workbook (replaceable with JPA). */
public final class InMemoryWorkbookRepository implements WorkbookRepository {
    private final Map<UUID, StoredWorkbook> store = new ConcurrentHashMap<>();

    @Override
    public Workbook save(Workbook aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredWorkbook.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Workbook> findById(WorkbookId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredWorkbook::toDomain);
    }

    @Override
    public Optional<Workbook> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredWorkbook::toDomain)
                .findFirst();
    }

    @Override
    public List<Workbook> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredWorkbook row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredWorkbook::toDomain)
                .toList();
    }

    @Override
    public void delete(WorkbookId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredWorkbook(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredWorkbook from(Workbook aggregate) {
            return new StoredWorkbook(
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

        Workbook toDomain() {
            return Workbook.rehydrate(
                    WorkbookId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    WorkbookStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
