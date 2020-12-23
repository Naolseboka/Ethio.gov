package et.gov.ethgov.gateway.application.healthcheck;

import et.gov.ethgov.gateway.domain.healthcheck.Healthcheck;
import et.gov.ethgov.gateway.domain.healthcheck.HealthcheckId;
import et.gov.ethgov.gateway.domain.healthcheck.HealthcheckRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivateHealthcheckUseCase {
    private final HealthcheckRepository repository;
    private final Clock clock;

    public ActivateHealthcheckUseCase(HealthcheckRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public HealthcheckId execute(ActivateHealthcheckCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        HealthcheckId id = HealthcheckId.parse(command.healthcheckId());
        Healthcheck aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("healthcheck not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivateHealthcheckCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String healthcheckId,
            String reason) {}
}
