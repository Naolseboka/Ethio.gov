package et.gov.ethgov.opendata.adapter.out.persistence;

import et.gov.ethgov.opendata.domain.datalicense.Datalicense;
import et.gov.ethgov.opendata.domain.datalicense.DatalicenseId;
import et.gov.ethgov.opendata.domain.datalicense.DatalicenseRepository;
import et.gov.ethgov.opendata.domain.datalicense.DatalicenseStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Datalicense (replaceable with JPA). */
public final class InMemoryDatalicenseRepository implements DatalicenseRepository {
    private final Map<UUID, StoredDatalicense> store = new ConcurrentHashMap<>();

    @Override
    public Datalicense save(Datalicense aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredDatalicense.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Datalicense> findById(DatalicenseId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredDatalicense::toDomain);
    }

    @Override
    public Optional<Datalicense> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredDatalicense::toDomain)
                .findFirst();
    }

    @Override
    public List<Datalicense> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredDatalicense row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredDatalicense::toDomain)
                .toList();
    }

    @Override
    public void delete(DatalicenseId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredDatalicense(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredDatalicense from(Datalicense aggregate) {
            return new StoredDatalicense(
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

        Datalicense toDomain() {
            return Datalicense.rehydrate(
                    DatalicenseId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    DatalicenseStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
