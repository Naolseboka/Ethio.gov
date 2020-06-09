package et.gov.ethgov.directory.domain.servicedesk;

import java.util.List;
import java.util.Optional;

public interface ServicedeskRepository {
    Servicedesk save(Servicedesk aggregate);

    Optional<Servicedesk> findById(ServicedeskId id);

    Optional<Servicedesk> findByReferenceCode(String tenantId, String referenceCode);

    List<Servicedesk> findByTenant(String tenantId, int limit);

    void delete(ServicedeskId id);
}
