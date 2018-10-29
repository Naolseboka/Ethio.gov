package et.gov.ethgov.analytics.domain.cohort;

import java.util.List;
import java.util.Optional;

public interface CohortRepository {
    Cohort save(Cohort aggregate);

    Optional<Cohort> findById(CohortId id);

    Optional<Cohort> findByReferenceCode(String tenantId, String referenceCode);

    List<Cohort> findByTenant(String tenantId, int limit);

    void delete(CohortId id);
}
