package et.gov.ethgov.licensing.adapter.out.persistence;

import et.gov.ethgov.licensing.domain.licensefee.Licensefee;
import et.gov.ethgov.licensing.domain.licensefee.LicensefeeId;
import et.gov.ethgov.licensing.domain.licensefee.LicensefeeRepository;
import et.gov.ethgov.licensing.domain.licensefee.LicensefeeStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Licensefee (replaceable with JPA). */
public final class InMemoryLicensefeeRepository implements LicensefeeRepository {
    private final Map<UUID, StoredLicensefee> store = new ConcurrentHashMap<>();

    @Override
    public Licensefee save(Licensefee aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredLicensefee.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Licensefee> findById(LicensefeeId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredLicensefee::toDomain);
    }

    @Override
    public Optional<Licensefee> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredLicensefee::toDomain)
                .findFirst();
    }

    @Override
    public List<Licensefee> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredLicensefee row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredLicensefee::toDomain)
                .toList();
    }

    @Override
    public void delete(LicensefeeId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredLicensefee(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredLicensefee from(Licensefee aggregate) {
            return new StoredLicensefee(
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

        Licensefee toDomain() {
            return Licensefee.rehydrate(
                    LicensefeeId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    LicensefeeStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
