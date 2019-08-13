package et.gov.ethgov.citizen.domain.disabilitystatus;

import java.util.List;
import java.util.Optional;

public interface DisabilitystatusRepository {
    Disabilitystatus save(Disabilitystatus aggregate);

    Optional<Disabilitystatus> findById(DisabilitystatusId id);

    Optional<Disabilitystatus> findByReferenceCode(String tenantId, String referenceCode);

    List<Disabilitystatus> findByTenant(String tenantId, int limit);

    void delete(DisabilitystatusId id);
}
