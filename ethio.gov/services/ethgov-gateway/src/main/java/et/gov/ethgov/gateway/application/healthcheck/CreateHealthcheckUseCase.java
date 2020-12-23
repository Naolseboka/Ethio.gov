package et.gov.ethgov.gateway.application.healthcheck;

import et.gov.ethgov.gateway.domain.healthcheck.Healthcheck;
import et.gov.ethgov.gateway.domain.healthcheck.HealthcheckId;
import et.gov.ethgov.gateway.domain.healthcheck.HealthcheckRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateHealthcheckUseCase {
    private final HealthcheckRepository repository;
    private final Clock clock;

    public CreateHealthcheckUseCase(HealthcheckRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public HealthcheckId execute(CreateHealthcheckCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Healthcheck aggregate = Healthcheck.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateHealthcheckCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String healthcheckId,
            String reason) {}
}
