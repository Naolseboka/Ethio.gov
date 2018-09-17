package et.gov.ethgov.analytics.application.event;

import et.gov.ethgov.analytics.domain.event.Event;
import et.gov.ethgov.analytics.domain.event.EventId;
import et.gov.ethgov.analytics.domain.event.EventRepository;
import java.util.Objects;

public final class GetEventUseCase {
    private final EventRepository repository;

    public GetEventUseCase(EventRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Event execute(String rawId) {
        EventId id = EventId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("event not found: " + rawId));
    }
}
