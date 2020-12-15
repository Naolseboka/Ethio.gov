package et.gov.ethgov.gateway.application.cors;

import et.gov.ethgov.gateway.domain.cors.Cors;
import et.gov.ethgov.gateway.domain.cors.CorsId;
import et.gov.ethgov.gateway.domain.cors.CorsRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateCorsUseCase {
    private final CorsRepository repository;
    private final Clock clock;

    public CreateCorsUseCase(CorsRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public CorsId execute(CreateCorsCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Cors aggregate = Cors.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateCorsCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String corsId,
            String reason) {}
}
