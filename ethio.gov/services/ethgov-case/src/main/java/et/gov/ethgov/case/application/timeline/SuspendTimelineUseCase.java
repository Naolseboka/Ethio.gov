package et.gov.ethgov.case.application.timeline;

import et.gov.ethgov.case.domain.timeline.Timeline;
import et.gov.ethgov.case.domain.timeline.TimelineId;
import et.gov.ethgov.case.domain.timeline.TimelineRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendTimelineUseCase {
    private final TimelineRepository repository;
    private final Clock clock;

    public SuspendTimelineUseCase(TimelineRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public TimelineId execute(SuspendTimelineCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        TimelineId id = TimelineId.parse(command.timelineId());
        Timeline aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("timeline not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendTimelineCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String timelineId,
            String reason) {}
}
