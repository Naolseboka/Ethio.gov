package et.gov.ethgov.document.application.retention;

import et.gov.ethgov.document.domain.retention.Retention;
import et.gov.ethgov.document.domain.retention.RetentionId;
import et.gov.ethgov.document.domain.retention.RetentionRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivateRetentionUseCase {
    private final RetentionRepository repository;
    private final Clock clock;

    public ActivateRetentionUseCase(RetentionRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public RetentionId execute(ActivateRetentionCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        RetentionId id = RetentionId.parse(command.retentionId());
        Retention aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("retention not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivateRetentionCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String retentionId,
            String reason) {}
}
