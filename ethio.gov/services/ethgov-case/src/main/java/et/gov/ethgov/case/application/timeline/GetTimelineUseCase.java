package et.gov.ethgov.case.application.timeline;

import et.gov.ethgov.case.domain.timeline.Timeline;
import et.gov.ethgov.case.domain.timeline.TimelineId;
import et.gov.ethgov.case.domain.timeline.TimelineRepository;
import java.util.Objects;

public final class GetTimelineUseCase {
    private final TimelineRepository repository;

    public GetTimelineUseCase(TimelineRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Timeline execute(String rawId) {
        TimelineId id = TimelineId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("timeline not found: " + rawId));
    }
}
