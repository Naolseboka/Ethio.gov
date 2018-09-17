package et.gov.ethgov.analytics.application.event;

import et.gov.ethgov.analytics.domain.event.Event;
import et.gov.ethgov.analytics.domain.event.EventId;
import et.gov.ethgov.analytics.domain.event.EventRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendEventUseCase {
    private final EventRepository repository;
    private final Clock clock;

    public SuspendEventUseCase(EventRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public EventId execute(SuspendEventCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        EventId id = EventId.parse(command.eventId());
        Event aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("event not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendEventCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String eventId,
            String reason) {}
}
