package et.gov.ethgov.case.adapter.out.persistence;

import et.gov.ethgov.case.domain.hearing.Hearing;
import et.gov.ethgov.case.domain.hearing.HearingId;
import et.gov.ethgov.case.domain.hearing.HearingRepository;
import et.gov.ethgov.case.domain.hearing.HearingStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Hearing (replaceable with JPA). */
public final class InMemoryHearingRepository implements HearingRepository {
    private final Map<UUID, StoredHearing> store = new ConcurrentHashMap<>();

    @Override
    public Hearing save(Hearing aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredHearing.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Hearing> findById(HearingId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredHearing::toDomain);
    }

    @Override
    public Optional<Hearing> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredHearing::toDomain)
                .findFirst();
    }

    @Override
    public List<Hearing> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredHearing row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredHearing::toDomain)
                .toList();
    }

    @Override
    public void delete(HearingId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredHearing(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredHearing from(Hearing aggregate) {
            return new StoredHearing(
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

        Hearing toDomain() {
            return Hearing.rehydrate(
                    HearingId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    HearingStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
