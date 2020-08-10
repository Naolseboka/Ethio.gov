package et.gov.ethgov.document.application.redaction;

import et.gov.ethgov.document.domain.redaction.Redaction;
import et.gov.ethgov.document.domain.redaction.RedactionId;
import et.gov.ethgov.document.domain.redaction.RedactionRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateRedactionUseCase {
    private final RedactionRepository repository;
    private final Clock clock;

    public CreateRedactionUseCase(RedactionRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public RedactionId execute(CreateRedactionCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Redaction aggregate = Redaction.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateRedactionCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String redactionId,
            String reason) {}
}
