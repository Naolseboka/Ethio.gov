package et.gov.ethgov.document.application.redaction;

import et.gov.ethgov.document.domain.redaction.Redaction;
import et.gov.ethgov.document.domain.redaction.RedactionId;
import et.gov.ethgov.document.domain.redaction.RedactionRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateRedactionUseCase {
    private final RedactionRepository repository;
    private final Clock clock;

    public UpdateRedactionUseCase(RedactionRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public RedactionId execute(UpdateRedactionCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        RedactionId id = RedactionId.parse(command.redactionId());
        Redaction aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("redaction not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateRedactionCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String redactionId,
            String reason) {}
}
