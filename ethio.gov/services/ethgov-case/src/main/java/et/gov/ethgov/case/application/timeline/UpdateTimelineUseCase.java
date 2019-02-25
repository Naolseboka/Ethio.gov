package et.gov.ethgov.case.application.timeline;

import et.gov.ethgov.case.domain.timeline.Timeline;
import et.gov.ethgov.case.domain.timeline.TimelineId;
import et.gov.ethgov.case.domain.timeline.TimelineRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateTimelineUseCase {
    private final TimelineRepository repository;
    private final Clock clock;

    public UpdateTimelineUseCase(TimelineRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public TimelineId execute(UpdateTimelineCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        TimelineId id = TimelineId.parse(command.timelineId());
        Timeline aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("timeline not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateTimelineCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String timelineId,
            String reason) {}
}
