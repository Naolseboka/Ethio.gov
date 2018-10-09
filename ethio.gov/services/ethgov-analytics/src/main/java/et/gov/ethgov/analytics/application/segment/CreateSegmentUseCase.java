package et.gov.ethgov.analytics.application.segment;

import et.gov.ethgov.analytics.domain.segment.Segment;
import et.gov.ethgov.analytics.domain.segment.SegmentId;
import et.gov.ethgov.analytics.domain.segment.SegmentRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateSegmentUseCase {
    private final SegmentRepository repository;
    private final Clock clock;

    public CreateSegmentUseCase(SegmentRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public SegmentId execute(CreateSegmentCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Segment aggregate = Segment.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateSegmentCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String segmentId,
            String reason) {}
}
