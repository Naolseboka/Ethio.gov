package et.gov.ethgov.gateway.application.route;

import et.gov.ethgov.gateway.domain.route.Route;
import et.gov.ethgov.gateway.domain.route.RouteId;
import et.gov.ethgov.gateway.domain.route.RouteRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateRouteUseCase {
    private final RouteRepository repository;
    private final Clock clock;

    public CreateRouteUseCase(RouteRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public RouteId execute(CreateRouteCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Route aggregate = Route.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateRouteCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String routeId,
            String reason) {}
}
