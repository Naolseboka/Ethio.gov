package et.gov.ethgov.revenue.domain.taxbill;

import java.util.List;
import java.util.Optional;

public interface TaxbillRepository {
    Taxbill save(Taxbill aggregate);

    Optional<Taxbill> findById(TaxbillId id);

    Optional<Taxbill> findByReferenceCode(String tenantId, String referenceCode);

    List<Taxbill> findByTenant(String tenantId, int limit);

    void delete(TaxbillId id);
}
