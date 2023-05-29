package et.gov.ethgov.opendata.domain.portalpage;

import java.util.List;
import java.util.Optional;

public interface PortalpageRepository {
    Portalpage save(Portalpage aggregate);

    Optional<Portalpage> findById(PortalpageId id);

    Optional<Portalpage> findByReferenceCode(String tenantId, String referenceCode);

    List<Portalpage> findByTenant(String tenantId, int limit);

    void delete(PortalpageId id);
}
