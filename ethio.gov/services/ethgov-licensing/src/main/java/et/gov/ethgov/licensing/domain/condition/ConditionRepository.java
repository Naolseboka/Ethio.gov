package et.gov.ethgov.licensing.domain.condition;

import java.util.List;
import java.util.Optional;

public interface ConditionRepository {
    Condition save(Condition aggregate);

    Optional<Condition> findById(ConditionId id);

    Optional<Condition> findByReferenceCode(String tenantId, String referenceCode);

    List<Condition> findByTenant(String tenantId, int limit);

    void delete(ConditionId id);
}
