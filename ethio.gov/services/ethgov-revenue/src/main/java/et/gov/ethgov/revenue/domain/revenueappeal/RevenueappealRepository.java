package et.gov.ethgov.revenue.domain.revenueappeal;

import java.util.List;
import java.util.Optional;

public interface RevenueappealRepository {
    Revenueappeal save(Revenueappeal aggregate);

    Optional<Revenueappeal> findById(RevenueappealId id);

    Optional<Revenueappeal> findByReferenceCode(String tenantId, String referenceCode);

    List<Revenueappeal> findByTenant(String tenantId, int limit);

    void delete(RevenueappealId id);
}
