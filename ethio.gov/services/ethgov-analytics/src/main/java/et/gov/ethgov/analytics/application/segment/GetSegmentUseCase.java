package et.gov.ethgov.analytics.application.segment;

import et.gov.ethgov.analytics.domain.segment.Segment;
import et.gov.ethgov.analytics.domain.segment.SegmentId;
import et.gov.ethgov.analytics.domain.segment.SegmentRepository;
import java.util.Objects;

public final class GetSegmentUseCase {
    private final SegmentRepository repository;

    public GetSegmentUseCase(SegmentRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Segment execute(String rawId) {
        SegmentId id = SegmentId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("segment not found: " + rawId));
    }
}
