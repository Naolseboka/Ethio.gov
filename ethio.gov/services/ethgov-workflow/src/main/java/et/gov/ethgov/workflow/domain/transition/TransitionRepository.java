package et.gov.ethgov.workflow.domain.transition;

import java.util.List;
import java.util.Optional;

public interface TransitionRepository {
    Transition save(Transition aggregate);

    Optional<Transition> findById(TransitionId id);

    Optional<Transition> findByReferenceCode(String tenantId, String referenceCode);

    List<Transition> findByTenant(String tenantId, int limit);

    void delete(TransitionId id);
}
