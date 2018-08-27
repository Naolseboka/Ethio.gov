package et.gov.ethgov.analytics.adapter.out.persistence;

import et.gov.ethgov.analytics.domain.event.Event;
import et.gov.ethgov.analytics.domain.event.EventId;
import et.gov.ethgov.analytics.domain.event.EventRepository;
import et.gov.ethgov.analytics.domain.event.EventStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Event (replaceable with JPA). */
public final class InMemoryEventRepository implements EventRepository {
    private final Map<UUID, StoredEvent> store = new ConcurrentHashMap<>();

    @Override
    public Event save(Event aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredEvent.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Event> findById(EventId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredEvent::toDomain);
    }

    @Override
    public Optional<Event> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredEvent::toDomain)
                .findFirst();
    }

    @Override
    public List<Event> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredEvent row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredEvent::toDomain)
                .toList();
    }

    @Override
    public void delete(EventId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredEvent(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredEvent from(Event aggregate) {
            return new StoredEvent(
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

        Event toDomain() {
            return Event.rehydrate(
                    EventId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    EventStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
