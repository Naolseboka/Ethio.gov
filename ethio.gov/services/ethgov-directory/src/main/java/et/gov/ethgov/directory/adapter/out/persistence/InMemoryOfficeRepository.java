package et.gov.ethgov.directory.adapter.out.persistence;

import et.gov.ethgov.directory.domain.office.Office;
import et.gov.ethgov.directory.domain.office.OfficeId;
import et.gov.ethgov.directory.domain.office.OfficeRepository;
import et.gov.ethgov.directory.domain.office.OfficeStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Office (replaceable with JPA). */
public final class InMemoryOfficeRepository implements OfficeRepository {
    private final Map<UUID, StoredOffice> store = new ConcurrentHashMap<>();

    @Override
    public Office save(Office aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredOffice.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Office> findById(OfficeId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredOffice::toDomain);
    }

    @Override
    public Optional<Office> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredOffice::toDomain)
                .findFirst();
    }

    @Override
    public List<Office> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredOffice row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredOffice::toDomain)
                .toList();
    }

    @Override
    public void delete(OfficeId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredOffice(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredOffice from(Office aggregate) {
            return new StoredOffice(
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

        Office toDomain() {
            return Office.rehydrate(
                    OfficeId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    OfficeStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
