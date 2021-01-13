package et.gov.ethgov.gateway.application.route;

import et.gov.ethgov.gateway.domain.route.Route;
import et.gov.ethgov.gateway.domain.route.RouteId;
import et.gov.ethgov.gateway.domain.route.RouteRepository;
import java.util.Objects;

public final class GetRouteUseCase {
    private final RouteRepository repository;

    public GetRouteUseCase(RouteRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Route execute(String rawId) {
        RouteId id = RouteId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("route not found: " + rawId));
    }
}
