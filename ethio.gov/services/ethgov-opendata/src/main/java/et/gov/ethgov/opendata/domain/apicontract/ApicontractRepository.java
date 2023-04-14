package et.gov.ethgov.opendata.domain.apicontract;

import java.util.List;
import java.util.Optional;

public interface ApicontractRepository {
    Apicontract save(Apicontract aggregate);

    Optional<Apicontract> findById(ApicontractId id);

    Optional<Apicontract> findByReferenceCode(String tenantId, String referenceCode);

    List<Apicontract> findByTenant(String tenantId, int limit);

    void delete(ApicontractId id);
}
