package et.gov.ethgov.workflow.domain.assignment;

import java.util.List;
import java.util.Optional;

public interface AssignmentRepository {
    Assignment save(Assignment aggregate);

    Optional<Assignment> findById(AssignmentId id);

    Optional<Assignment> findByReferenceCode(String tenantId, String referenceCode);

    List<Assignment> findByTenant(String tenantId, int limit);

    void delete(AssignmentId id);
}
