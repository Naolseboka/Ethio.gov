package et.gov.ethgov.gateway.application.circuitbreaker;

import et.gov.ethgov.gateway.domain.circuitbreaker.Circuitbreaker;
import et.gov.ethgov.gateway.domain.circuitbreaker.CircuitbreakerId;
import et.gov.ethgov.gateway.domain.circuitbreaker.CircuitbreakerRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateCircuitbreakerUseCase {
    private final CircuitbreakerRepository repository;
    private final Clock clock;

    public CreateCircuitbreakerUseCase(CircuitbreakerRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public CircuitbreakerId execute(CreateCircuitbreakerCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Circuitbreaker aggregate = Circuitbreaker.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateCircuitbreakerCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String circuitbreakerId,
            String reason) {}
}
