package et.gov.ethgov.case.domain.appeal;

import java.util.List;
import java.util.Optional;

public interface AppealRepository {
    Appeal save(Appeal aggregate);

    Optional<Appeal> findById(AppealId id);

    Optional<Appeal> findByReferenceCode(String tenantId, String referenceCode);

    List<Appeal> findByTenant(String tenantId, int limit);

    void delete(AppealId id);
}
