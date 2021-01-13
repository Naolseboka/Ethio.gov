package et.gov.ethgov.gateway.application.route;

import et.gov.ethgov.gateway.domain.route.Route;
import et.gov.ethgov.gateway.domain.route.RouteRepository;
import java.util.List;
import java.util.Objects;

public final class ListRouteUseCase {
    private final RouteRepository repository;

    public ListRouteUseCase(RouteRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Route> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
