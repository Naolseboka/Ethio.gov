package et.gov.ethgov.licensing.adapter.out.persistence;

import et.gov.ethgov.licensing.domain.license.License;
import et.gov.ethgov.licensing.domain.license.LicenseId;
import et.gov.ethgov.licensing.domain.license.LicenseRepository;
import et.gov.ethgov.licensing.domain.license.LicenseStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for License (replaceable with JPA). */
public final class InMemoryLicenseRepository implements LicenseRepository {
    private final Map<UUID, StoredLicense> store = new ConcurrentHashMap<>();

    @Override
    public License save(License aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredLicense.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<License> findById(LicenseId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredLicense::toDomain);
    }

    @Override
    public Optional<License> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredLicense::toDomain)
                .findFirst();
    }

    @Override
    public List<License> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredLicense row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredLicense::toDomain)
                .toList();
    }

    @Override
    public void delete(LicenseId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredLicense(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredLicense from(License aggregate) {
            return new StoredLicense(
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

        License toDomain() {
            return License.rehydrate(
                    LicenseId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    LicenseStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
