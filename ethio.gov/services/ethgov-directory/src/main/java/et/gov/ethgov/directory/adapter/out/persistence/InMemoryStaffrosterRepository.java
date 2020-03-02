package et.gov.ethgov.directory.adapter.out.persistence;

import et.gov.ethgov.directory.domain.staffroster.Staffroster;
import et.gov.ethgov.directory.domain.staffroster.StaffrosterId;
import et.gov.ethgov.directory.domain.staffroster.StaffrosterRepository;
import et.gov.ethgov.directory.domain.staffroster.StaffrosterStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Staffroster (replaceable with JPA). */
public final class InMemoryStaffrosterRepository implements StaffrosterRepository {
    private final Map<UUID, StoredStaffroster> store = new ConcurrentHashMap<>();

    @Override
    public Staffroster save(Staffroster aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredStaffroster.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Staffroster> findById(StaffrosterId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredStaffroster::toDomain);
    }

    @Override
    public Optional<Staffroster> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredStaffroster::toDomain)
                .findFirst();
    }

    @Override
    public List<Staffroster> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredStaffroster row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredStaffroster::toDomain)
                .toList();
    }

    @Override
    public void delete(StaffrosterId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredStaffroster(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredStaffroster from(Staffroster aggregate) {
            return new StoredStaffroster(
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

        Staffroster toDomain() {
            return Staffroster.rehydrate(
                    StaffrosterId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    StaffrosterStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
