package et.gov.ethgov.document.application.documentset;

import et.gov.ethgov.document.domain.documentset.Documentset;
import et.gov.ethgov.document.domain.documentset.DocumentsetId;
import et.gov.ethgov.document.domain.documentset.DocumentsetRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateDocumentsetUseCase {
    private final DocumentsetRepository repository;
    private final Clock clock;

    public CreateDocumentsetUseCase(DocumentsetRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public DocumentsetId execute(CreateDocumentsetCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Documentset aggregate = Documentset.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateDocumentsetCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String documentsetId,
            String reason) {}
}
