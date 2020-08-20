package et.gov.ethgov.document.application.scanbatch;

import et.gov.ethgov.document.domain.scanbatch.Scanbatch;
import et.gov.ethgov.document.domain.scanbatch.ScanbatchId;
import et.gov.ethgov.document.domain.scanbatch.ScanbatchRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateScanbatchUseCase {
    private final ScanbatchRepository repository;
    private final Clock clock;

    public CreateScanbatchUseCase(ScanbatchRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public ScanbatchId execute(CreateScanbatchCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Scanbatch aggregate = Scanbatch.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateScanbatchCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String scanbatchId,
            String reason) {}
}
