package et.gov.ethgov.case.domain.priority;

import java.util.List;
import java.util.Optional;

public interface PriorityRepository {
    Priority save(Priority aggregate);

    Optional<Priority> findById(PriorityId id);

    Optional<Priority> findByReferenceCode(String tenantId, String referenceCode);

    List<Priority> findByTenant(String tenantId, int limit);

    void delete(PriorityId id);
}
