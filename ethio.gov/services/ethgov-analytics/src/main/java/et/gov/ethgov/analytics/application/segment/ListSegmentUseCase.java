package et.gov.ethgov.analytics.application.segment;

import et.gov.ethgov.analytics.domain.segment.Segment;
import et.gov.ethgov.analytics.domain.segment.SegmentRepository;
import java.util.List;
import java.util.Objects;

public final class ListSegmentUseCase {
    private final SegmentRepository repository;

    public ListSegmentUseCase(SegmentRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Segment> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
