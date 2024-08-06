package et.gov.ethgov.reporting.adapter.out.persistence;

import et.gov.ethgov.reporting.domain.report.Report;
import et.gov.ethgov.reporting.domain.report.ReportId;
import et.gov.ethgov.reporting.domain.report.ReportRepository;
import et.gov.ethgov.reporting.domain.report.ReportStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Report (replaceable with JPA). */
public final class InMemoryReportRepository implements ReportRepository {
    private final Map<UUID, StoredReport> store = new ConcurrentHashMap<>();

    @Override
    public Report save(Report aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredReport.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Report> findById(ReportId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredReport::toDomain);
    }

    @Override
    public Optional<Report> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredReport::toDomain)
                .findFirst();
    }

    @Override
    public List<Report> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredReport row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredReport::toDomain)
                .toList();
    }

    @Override
    public void delete(ReportId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredReport(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredReport from(Report aggregate) {
            return new StoredReport(
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

        Report toDomain() {
            return Report.rehydrate(
                    ReportId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    ReportStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
