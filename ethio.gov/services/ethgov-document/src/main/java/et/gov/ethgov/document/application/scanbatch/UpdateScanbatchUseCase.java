package et.gov.ethgov.document.application.scanbatch;

import et.gov.ethgov.document.domain.scanbatch.Scanbatch;
import et.gov.ethgov.document.domain.scanbatch.ScanbatchId;
import et.gov.ethgov.document.domain.scanbatch.ScanbatchRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateScanbatchUseCase {
    private final ScanbatchRepository repository;
    private final Clock clock;

    public UpdateScanbatchUseCase(ScanbatchRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public ScanbatchId execute(UpdateScanbatchCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        ScanbatchId id = ScanbatchId.parse(command.scanbatchId());
        Scanbatch aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("scanbatch not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateScanbatchCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String scanbatchId,
            String reason) {}
}
