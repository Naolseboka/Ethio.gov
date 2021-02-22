package et.gov.ethgov.gateway.domain.gwraterule;

import java.util.List;
import java.util.Optional;

public interface GwrateruleRepository {
    Gwraterule save(Gwraterule aggregate);

    Optional<Gwraterule> findById(GwrateruleId id);

    Optional<Gwraterule> findByReferenceCode(String tenantId, String referenceCode);

    List<Gwraterule> findByTenant(String tenantId, int limit);

    void delete(GwrateruleId id);
}
