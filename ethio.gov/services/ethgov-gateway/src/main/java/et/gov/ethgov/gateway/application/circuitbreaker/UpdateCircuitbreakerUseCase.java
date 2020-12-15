package et.gov.ethgov.gateway.application.circuitbreaker;

import et.gov.ethgov.gateway.domain.circuitbreaker.Circuitbreaker;
import et.gov.ethgov.gateway.domain.circuitbreaker.CircuitbreakerId;
import et.gov.ethgov.gateway.domain.circuitbreaker.CircuitbreakerRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateCircuitbreakerUseCase {
    private final CircuitbreakerRepository repository;
    private final Clock clock;

    public UpdateCircuitbreakerUseCase(CircuitbreakerRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public CircuitbreakerId execute(UpdateCircuitbreakerCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        CircuitbreakerId id = CircuitbreakerId.parse(command.circuitbreakerId());
        Circuitbreaker aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("circuitbreaker not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateCircuitbreakerCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String circuitbreakerId,
            String reason) {}
}
