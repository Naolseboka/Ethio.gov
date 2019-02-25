package et.gov.ethgov.case.application.timeline;

import et.gov.ethgov.case.domain.timeline.Timeline;
import et.gov.ethgov.case.domain.timeline.TimelineRepository;
import java.util.List;
import java.util.Objects;

public final class ListTimelineUseCase {
    private final TimelineRepository repository;

    public ListTimelineUseCase(TimelineRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Timeline> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
