package et.gov.ethgov.analytics.application.segment;

import et.gov.ethgov.analytics.domain.segment.Segment;
import et.gov.ethgov.analytics.domain.segment.SegmentId;
import et.gov.ethgov.analytics.domain.segment.SegmentRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivateSegmentUseCase {
    private final SegmentRepository repository;
    private final Clock clock;

    public ActivateSegmentUseCase(SegmentRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public SegmentId execute(ActivateSegmentCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        SegmentId id = SegmentId.parse(command.segmentId());
        Segment aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("segment not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivateSegmentCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String segmentId,
            String reason) {}
}
