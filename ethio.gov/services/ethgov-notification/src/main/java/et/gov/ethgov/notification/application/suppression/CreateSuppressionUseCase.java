package et.gov.ethgov.notification.application.suppression;

import et.gov.ethgov.notification.domain.suppression.Suppression;
import et.gov.ethgov.notification.domain.suppression.SuppressionId;
import et.gov.ethgov.notification.domain.suppression.SuppressionRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateSuppressionUseCase {
    private final SuppressionRepository repository;
    private final Clock clock;

    public CreateSuppressionUseCase(SuppressionRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public SuppressionId execute(CreateSuppressionCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Suppression aggregate = Suppression.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateSuppressionCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String suppressionId,
            String reason) {}
}
