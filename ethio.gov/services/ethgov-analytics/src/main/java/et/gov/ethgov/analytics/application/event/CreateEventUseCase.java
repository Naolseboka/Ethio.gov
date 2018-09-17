package et.gov.ethgov.analytics.application.event;

import et.gov.ethgov.analytics.domain.event.Event;
import et.gov.ethgov.analytics.domain.event.EventId;
import et.gov.ethgov.analytics.domain.event.EventRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateEventUseCase {
    private final EventRepository repository;
    private final Clock clock;

    public CreateEventUseCase(EventRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public EventId execute(CreateEventCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Event aggregate = Event.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateEventCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String eventId,
            String reason) {}
}
