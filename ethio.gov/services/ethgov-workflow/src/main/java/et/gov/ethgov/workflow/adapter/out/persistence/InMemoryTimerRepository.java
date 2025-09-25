package et.gov.ethgov.workflow.adapter.out.persistence;

import et.gov.ethgov.workflow.domain.timer.Timer;
import et.gov.ethgov.workflow.domain.timer.TimerId;
import et.gov.ethgov.workflow.domain.timer.TimerRepository;
import et.gov.ethgov.workflow.domain.timer.TimerStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Timer (replaceable with JPA). */
public final class InMemoryTimerRepository implements TimerRepository {
    private final Map<UUID, StoredTimer> store = new ConcurrentHashMap<>();

    @Override
    public Timer save(Timer aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredTimer.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Timer> findById(TimerId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredTimer::toDomain);
    }

    @Override
    public Optional<Timer> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredTimer::toDomain)
                .findFirst();
    }

    @Override
    public List<Timer> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredTimer row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredTimer::toDomain)
                .toList();
    }

    @Override
    public void delete(TimerId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredTimer(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredTimer from(Timer aggregate) {
            return new StoredTimer(
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

        Timer toDomain() {
            return Timer.rehydrate(
                    TimerId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    TimerStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
