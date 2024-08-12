package et.gov.ethgov.reporting.adapter.out.persistence;

import et.gov.ethgov.reporting.domain.reportexport.Reportexport;
import et.gov.ethgov.reporting.domain.reportexport.ReportexportId;
import et.gov.ethgov.reporting.domain.reportexport.ReportexportRepository;
import et.gov.ethgov.reporting.domain.reportexport.ReportexportStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Reportexport (replaceable with JPA). */
public final class InMemoryReportexportRepository implements ReportexportRepository {
    private final Map<UUID, StoredReportexport> store = new ConcurrentHashMap<>();

    @Override
    public Reportexport save(Reportexport aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredReportexport.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Reportexport> findById(ReportexportId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredReportexport::toDomain);
    }

    @Override
    public Optional<Reportexport> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredReportexport::toDomain)
                .findFirst();
    }

    @Override
    public List<Reportexport> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredReportexport row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredReportexport::toDomain)
                .toList();
    }

    @Override
    public void delete(ReportexportId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredReportexport(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredReportexport from(Reportexport aggregate) {
            return new StoredReportexport(
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

        Reportexport toDomain() {
            return Reportexport.rehydrate(
                    ReportexportId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    ReportexportStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
