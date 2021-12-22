package et.gov.ethgov.identity.domain.riskscore;

import java.util.List;
import java.util.Optional;

public interface RiskscoreRepository {
    Riskscore save(Riskscore aggregate);

    Optional<Riskscore> findById(RiskscoreId id);

    Optional<Riskscore> findByReferenceCode(String tenantId, String referenceCode);

    List<Riskscore> findByTenant(String tenantId, int limit);

    void delete(RiskscoreId id);
}
