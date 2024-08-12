package et.gov.ethgov.reporting.adapter.out.persistence;

import et.gov.ethgov.reporting.domain.reportsubscription.Reportsubscription;
import et.gov.ethgov.reporting.domain.reportsubscription.ReportsubscriptionId;
import et.gov.ethgov.reporting.domain.reportsubscription.ReportsubscriptionRepository;
import et.gov.ethgov.reporting.domain.reportsubscription.ReportsubscriptionStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Reportsubscription (replaceable with JPA). */
public final class InMemoryReportsubscriptionRepository implements ReportsubscriptionRepository {
    private final Map<UUID, StoredReportsubscription> store = new ConcurrentHashMap<>();

    @Override
    public Reportsubscription save(Reportsubscription aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredReportsubscription.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Reportsubscription> findById(ReportsubscriptionId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredReportsubscription::toDomain);
    }

    @Override
    public Optional<Reportsubscription> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredReportsubscription::toDomain)
                .findFirst();
    }

    @Override
    public List<Reportsubscription> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredReportsubscription row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredReportsubscription::toDomain)
                .toList();
    }

    @Override
    public void delete(ReportsubscriptionId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredReportsubscription(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredReportsubscription from(Reportsubscription aggregate) {
            return new StoredReportsubscription(
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

        Reportsubscription toDomain() {
            return Reportsubscription.rehydrate(
                    ReportsubscriptionId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    ReportsubscriptionStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
