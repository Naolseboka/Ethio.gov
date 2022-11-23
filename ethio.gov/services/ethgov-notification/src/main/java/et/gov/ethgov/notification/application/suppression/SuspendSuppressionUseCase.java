package et.gov.ethgov.notification.application.suppression;

import et.gov.ethgov.notification.domain.suppression.Suppression;
import et.gov.ethgov.notification.domain.suppression.SuppressionId;
import et.gov.ethgov.notification.domain.suppression.SuppressionRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendSuppressionUseCase {
    private final SuppressionRepository repository;
    private final Clock clock;

    public SuspendSuppressionUseCase(SuppressionRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public SuppressionId execute(SuspendSuppressionCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        SuppressionId id = SuppressionId.parse(command.suppressionId());
        Suppression aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("suppression not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendSuppressionCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String suppressionId,
            String reason) {}
}
