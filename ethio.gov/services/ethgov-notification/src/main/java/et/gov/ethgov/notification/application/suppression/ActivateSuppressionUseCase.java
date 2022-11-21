package et.gov.ethgov.notification.application.suppression;

import et.gov.ethgov.notification.domain.suppression.Suppression;
import et.gov.ethgov.notification.domain.suppression.SuppressionId;
import et.gov.ethgov.notification.domain.suppression.SuppressionRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivateSuppressionUseCase {
    private final SuppressionRepository repository;
    private final Clock clock;

    public ActivateSuppressionUseCase(SuppressionRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public SuppressionId execute(ActivateSuppressionCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        SuppressionId id = SuppressionId.parse(command.suppressionId());
        Suppression aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("suppression not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivateSuppressionCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String suppressionId,
            String reason) {}
}
