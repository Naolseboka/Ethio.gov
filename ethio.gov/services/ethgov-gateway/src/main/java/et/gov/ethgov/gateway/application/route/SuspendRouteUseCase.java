package et.gov.ethgov.gateway.application.route;

import et.gov.ethgov.gateway.domain.route.Route;
import et.gov.ethgov.gateway.domain.route.RouteId;
import et.gov.ethgov.gateway.domain.route.RouteRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendRouteUseCase {
    private final RouteRepository repository;
    private final Clock clock;

    public SuspendRouteUseCase(RouteRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public RouteId execute(SuspendRouteCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        RouteId id = RouteId.parse(command.routeId());
        Route aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("route not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendRouteCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String routeId,
            String reason) {}
}
