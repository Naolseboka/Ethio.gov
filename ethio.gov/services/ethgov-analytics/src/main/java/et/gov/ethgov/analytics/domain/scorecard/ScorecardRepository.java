package et.gov.ethgov.analytics.domain.scorecard;

import java.util.List;
import java.util.Optional;

public interface ScorecardRepository {
    Scorecard save(Scorecard aggregate);

    Optional<Scorecard> findById(ScorecardId id);

    Optional<Scorecard> findByReferenceCode(String tenantId, String referenceCode);

    List<Scorecard> findByTenant(String tenantId, int limit);

    void delete(ScorecardId id);
}
