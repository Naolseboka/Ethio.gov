package et.gov.ethgov.revenue.domain.assessment;

import java.util.List;
import java.util.Optional;

public interface AssessmentRepository {
    Assessment save(Assessment aggregate);

    Optional<Assessment> findById(AssessmentId id);

    Optional<Assessment> findByReferenceCode(String tenantId, String referenceCode);

    List<Assessment> findByTenant(String tenantId, int limit);

    void delete(AssessmentId id);
}
