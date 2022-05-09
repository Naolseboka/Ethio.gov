package et.gov.ethgov.land.domain.valuation;

import java.util.List;
import java.util.Optional;

public interface ValuationRepository {
    Valuation save(Valuation aggregate);

    Optional<Valuation> findById(ValuationId id);

    Optional<Valuation> findByReferenceCode(String tenantId, String referenceCode);

    List<Valuation> findByTenant(String tenantId, int limit);

    void delete(ValuationId id);
}
