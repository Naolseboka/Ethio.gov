package et.gov.ethgov.analytics.domain.segment;

import java.util.List;
import java.util.Optional;

public interface SegmentRepository {
    Segment save(Segment aggregate);

    Optional<Segment> findById(SegmentId id);

    Optional<Segment> findByReferenceCode(String tenantId, String referenceCode);

    List<Segment> findByTenant(String tenantId, int limit);

    void delete(SegmentId id);
}
