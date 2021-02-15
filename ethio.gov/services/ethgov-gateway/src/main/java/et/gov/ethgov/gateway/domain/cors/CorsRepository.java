package et.gov.ethgov.gateway.domain.cors;

import java.util.List;
import java.util.Optional;

public interface CorsRepository {
    Cors save(Cors aggregate);

    Optional<Cors> findById(CorsId id);

    Optional<Cors> findByReferenceCode(String tenantId, String referenceCode);

    List<Cors> findByTenant(String tenantId, int limit);

    void delete(CorsId id);
}
