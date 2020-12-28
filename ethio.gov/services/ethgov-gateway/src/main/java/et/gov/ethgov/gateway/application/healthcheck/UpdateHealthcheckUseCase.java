package et.gov.ethgov.gateway.application.healthcheck;

import et.gov.ethgov.gateway.domain.healthcheck.Healthcheck;
import et.gov.ethgov.gateway.domain.healthcheck.HealthcheckId;
import et.gov.ethgov.gateway.domain.healthcheck.HealthcheckRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateHealthcheckUseCase {
    private final HealthcheckRepository repository;
    private final Clock clock;

    public UpdateHealthcheckUseCase(HealthcheckRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public HealthcheckId execute(UpdateHealthcheckCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        HealthcheckId id = HealthcheckId.parse(command.healthcheckId());
        Healthcheck aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("healthcheck not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateHealthcheckCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String healthcheckId,
            String reason) {}
}
