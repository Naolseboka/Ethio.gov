package et.gov.ethgov.case.application.timeline;

import et.gov.ethgov.case.domain.timeline.Timeline;
import et.gov.ethgov.case.domain.timeline.TimelineId;
import et.gov.ethgov.case.domain.timeline.TimelineRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateTimelineUseCase {
    private final TimelineRepository repository;
    private final Clock clock;

    public CreateTimelineUseCase(TimelineRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public TimelineId execute(CreateTimelineCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Timeline aggregate = Timeline.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateTimelineCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String timelineId,
            String reason) {}
}
