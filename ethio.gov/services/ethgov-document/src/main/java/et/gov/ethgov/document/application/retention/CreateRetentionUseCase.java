package et.gov.ethgov.document.application.retention;

import et.gov.ethgov.document.domain.retention.Retention;
import et.gov.ethgov.document.domain.retention.RetentionId;
import et.gov.ethgov.document.domain.retention.RetentionRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateRetentionUseCase {
    private final RetentionRepository repository;
    private final Clock clock;

    public CreateRetentionUseCase(RetentionRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public RetentionId execute(CreateRetentionCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Retention aggregate = Retention.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateRetentionCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String retentionId,
            String reason) {}
}
