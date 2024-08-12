package et.gov.ethgov.reporting.adapter.out.persistence;

import et.gov.ethgov.reporting.domain.reportalert.Reportalert;
import et.gov.ethgov.reporting.domain.reportalert.ReportalertId;
import et.gov.ethgov.reporting.domain.reportalert.ReportalertRepository;
import et.gov.ethgov.reporting.domain.reportalert.ReportalertStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Reportalert (replaceable with JPA). */
public final class InMemoryReportalertRepository implements ReportalertRepository {
    private final Map<UUID, StoredReportalert> store = new ConcurrentHashMap<>();

    @Override
    public Reportalert save(Reportalert aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredReportalert.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Reportalert> findById(ReportalertId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredReportalert::toDomain);
    }

    @Override
    public Optional<Reportalert> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredReportalert::toDomain)
                .findFirst();
    }

    @Override
    public List<Reportalert> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredReportalert row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredReportalert::toDomain)
                .toList();
    }

    @Override
    public void delete(ReportalertId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredReportalert(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredReportalert from(Reportalert aggregate) {
            return new StoredReportalert(
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

        Reportalert toDomain() {
            return Reportalert.rehydrate(
                    ReportalertId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    ReportalertStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
