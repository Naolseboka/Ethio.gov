package et.gov.ethgov.gateway.application.canary;

import et.gov.ethgov.gateway.domain.canary.Canary;
import et.gov.ethgov.gateway.domain.canary.CanaryId;
import et.gov.ethgov.gateway.domain.canary.CanaryRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateCanaryUseCase {
    private final CanaryRepository repository;
    private final Clock clock;

    public CreateCanaryUseCase(CanaryRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public CanaryId execute(CreateCanaryCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Canary aggregate = Canary.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateCanaryCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String canaryId,
            String reason) {}
}
