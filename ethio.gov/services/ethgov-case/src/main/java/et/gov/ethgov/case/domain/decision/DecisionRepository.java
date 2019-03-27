package et.gov.ethgov.case.domain.decision;

import java.util.List;
import java.util.Optional;

public interface DecisionRepository {
    Decision save(Decision aggregate);

    Optional<Decision> findById(DecisionId id);

    Optional<Decision> findByReferenceCode(String tenantId, String referenceCode);

    List<Decision> findByTenant(String tenantId, int limit);

    void delete(DecisionId id);
}
