package et.gov.ethgov.citizen.domain.vitalevent;

import java.util.List;
import java.util.Optional;

public interface VitaleventRepository {
    Vitalevent save(Vitalevent aggregate);

    Optional<Vitalevent> findById(VitaleventId id);

    Optional<Vitalevent> findByReferenceCode(String tenantId, String referenceCode);

    List<Vitalevent> findByTenant(String tenantId, int limit);

    void delete(VitaleventId id);
}
