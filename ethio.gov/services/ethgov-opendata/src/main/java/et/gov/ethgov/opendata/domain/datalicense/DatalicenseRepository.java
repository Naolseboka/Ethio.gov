package et.gov.ethgov.opendata.domain.datalicense;

import java.util.List;
import java.util.Optional;

public interface DatalicenseRepository {
    Datalicense save(Datalicense aggregate);

    Optional<Datalicense> findById(DatalicenseId id);

    Optional<Datalicense> findByReferenceCode(String tenantId, String referenceCode);

    List<Datalicense> findByTenant(String tenantId, int limit);

    void delete(DatalicenseId id);
}
