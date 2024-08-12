package et.gov.ethgov.reporting.adapter.out.persistence;

import et.gov.ethgov.reporting.domain.schedule.Schedule;
import et.gov.ethgov.reporting.domain.schedule.ScheduleId;
import et.gov.ethgov.reporting.domain.schedule.ScheduleRepository;
import et.gov.ethgov.reporting.domain.schedule.ScheduleStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Schedule (replaceable with JPA). */
public final class InMemoryScheduleRepository implements ScheduleRepository {
    private final Map<UUID, StoredSchedule> store = new ConcurrentHashMap<>();

    @Override
    public Schedule save(Schedule aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredSchedule.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Schedule> findById(ScheduleId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredSchedule::toDomain);
    }

    @Override
    public Optional<Schedule> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredSchedule::toDomain)
                .findFirst();
    }

    @Override
    public List<Schedule> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredSchedule row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredSchedule::toDomain)
                .toList();
    }

    @Override
    public void delete(ScheduleId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredSchedule(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredSchedule from(Schedule aggregate) {
            return new StoredSchedule(
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

        Schedule toDomain() {
            return Schedule.rehydrate(
                    ScheduleId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    ScheduleStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
