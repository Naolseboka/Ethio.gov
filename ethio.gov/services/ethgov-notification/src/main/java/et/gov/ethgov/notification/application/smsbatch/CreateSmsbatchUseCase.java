package et.gov.ethgov.notification.application.smsbatch;

import et.gov.ethgov.notification.domain.smsbatch.Smsbatch;
import et.gov.ethgov.notification.domain.smsbatch.SmsbatchId;
import et.gov.ethgov.notification.domain.smsbatch.SmsbatchRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateSmsbatchUseCase {
    private final SmsbatchRepository repository;
    private final Clock clock;

    public CreateSmsbatchUseCase(SmsbatchRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public SmsbatchId execute(CreateSmsbatchCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Smsbatch aggregate = Smsbatch.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateSmsbatchCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String smsbatchId,
            String reason) {}
}
