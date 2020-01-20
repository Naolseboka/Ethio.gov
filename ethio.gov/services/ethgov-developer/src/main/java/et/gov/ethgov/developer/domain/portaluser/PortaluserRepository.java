package et.gov.ethgov.developer.domain.portaluser;

import java.util.List;
import java.util.Optional;

public interface PortaluserRepository {
    Portaluser save(Portaluser aggregate);

    Optional<Portaluser> findById(PortaluserId id);

    Optional<Portaluser> findByReferenceCode(String tenantId, String referenceCode);

    List<Portaluser> findByTenant(String tenantId, int limit);

    void delete(PortaluserId id);
}
