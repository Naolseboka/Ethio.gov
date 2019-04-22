package et.gov.ethgov.case.domain.timeline;

import java.util.List;
import java.util.Optional;

public interface TimelineRepository {
    Timeline save(Timeline aggregate);

    Optional<Timeline> findById(TimelineId id);

    Optional<Timeline> findByReferenceCode(String tenantId, String referenceCode);

    List<Timeline> findByTenant(String tenantId, int limit);

    void delete(TimelineId id);
}
