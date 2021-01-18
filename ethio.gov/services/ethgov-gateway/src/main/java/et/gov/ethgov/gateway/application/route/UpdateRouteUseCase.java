package et.gov.ethgov.gateway.application.route;

import et.gov.ethgov.gateway.domain.route.Route;
import et.gov.ethgov.gateway.domain.route.RouteId;
import et.gov.ethgov.gateway.domain.route.RouteRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateRouteUseCase {
    private final RouteRepository repository;
    private final Clock clock;

    public UpdateRouteUseCase(RouteRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public RouteId execute(UpdateRouteCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        RouteId id = RouteId.parse(command.routeId());
        Route aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("route not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateRouteCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String routeId,
            String reason) {}
}
