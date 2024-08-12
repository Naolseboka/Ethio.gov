package et.gov.ethgov.reporting.adapter.out.persistence;

import et.gov.ethgov.reporting.domain.reportfilter.Reportfilter;
import et.gov.ethgov.reporting.domain.reportfilter.ReportfilterId;
import et.gov.ethgov.reporting.domain.reportfilter.ReportfilterRepository;
import et.gov.ethgov.reporting.domain.reportfilter.ReportfilterStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Reportfilter (replaceable with JPA). */
public final class InMemoryReportfilterRepository implements ReportfilterRepository {
    private final Map<UUID, StoredReportfilter> store = new ConcurrentHashMap<>();

    @Override
    public Reportfilter save(Reportfilter aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredReportfilter.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Reportfilter> findById(ReportfilterId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredReportfilter::toDomain);
    }

    @Override
    public Optional<Reportfilter> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredReportfilter::toDomain)
                .findFirst();
    }

    @Override
    public List<Reportfilter> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredReportfilter row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredReportfilter::toDomain)
                .toList();
    }

    @Override
    public void delete(ReportfilterId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredReportfilter(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredReportfilter from(Reportfilter aggregate) {
            return new StoredReportfilter(
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

        Reportfilter toDomain() {
            return Reportfilter.rehydrate(
                    ReportfilterId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    ReportfilterStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
