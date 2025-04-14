package et.gov.ethgov.revenue.domain.rebate;

import java.util.List;
import java.util.Optional;

public interface RebateRepository {
    Rebate save(Rebate aggregate);

    Optional<Rebate> findById(RebateId id);

    Optional<Rebate> findByReferenceCode(String tenantId, String referenceCode);

    List<Rebate> findByTenant(String tenantId, int limit);

    void delete(RebateId id);
}
