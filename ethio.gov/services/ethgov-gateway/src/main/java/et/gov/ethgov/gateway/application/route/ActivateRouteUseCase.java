package et.gov.ethgov.gateway.application.route;

import et.gov.ethgov.gateway.domain.route.Route;
import et.gov.ethgov.gateway.domain.route.RouteId;
import et.gov.ethgov.gateway.domain.route.RouteRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivateRouteUseCase {
    private final RouteRepository repository;
    private final Clock clock;

    public ActivateRouteUseCase(RouteRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public RouteId execute(ActivateRouteCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        RouteId id = RouteId.parse(command.routeId());
        Route aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("route not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivateRouteCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String routeId,
            String reason) {}
}
