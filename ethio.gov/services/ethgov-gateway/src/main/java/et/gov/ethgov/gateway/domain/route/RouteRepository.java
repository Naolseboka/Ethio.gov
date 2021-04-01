package et.gov.ethgov.gateway.domain.route;

import java.util.List;
import java.util.Optional;

public interface RouteRepository {
    Route save(Route aggregate);

    Optional<Route> findById(RouteId id);

    Optional<Route> findByReferenceCode(String tenantId, String referenceCode);

    List<Route> findByTenant(String tenantId, int limit);

    void delete(RouteId id);
}
