package et.gov.ethgov.notification.application.smsbatch;

import et.gov.ethgov.notification.domain.smsbatch.Smsbatch;
import et.gov.ethgov.notification.domain.smsbatch.SmsbatchId;
import et.gov.ethgov.notification.domain.smsbatch.SmsbatchRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateSmsbatchUseCase {
    private final SmsbatchRepository repository;
    private final Clock clock;

    public UpdateSmsbatchUseCase(SmsbatchRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public SmsbatchId execute(UpdateSmsbatchCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        SmsbatchId id = SmsbatchId.parse(command.smsbatchId());
        Smsbatch aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("smsbatch not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateSmsbatchCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String smsbatchId,
            String reason) {}
}
