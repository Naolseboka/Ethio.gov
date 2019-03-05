package et.gov.ethgov.case.domain.caseassignment;

import java.util.List;
import java.util.Optional;

public interface CaseassignmentRepository {
    Caseassignment save(Caseassignment aggregate);

    Optional<Caseassignment> findById(CaseassignmentId id);

    Optional<Caseassignment> findByReferenceCode(String tenantId, String referenceCode);

    List<Caseassignment> findByTenant(String tenantId, int limit);

    void delete(CaseassignmentId id);
}
