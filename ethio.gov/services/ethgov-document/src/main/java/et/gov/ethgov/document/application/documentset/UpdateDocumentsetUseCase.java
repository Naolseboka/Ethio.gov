package et.gov.ethgov.document.application.documentset;

import et.gov.ethgov.document.domain.documentset.Documentset;
import et.gov.ethgov.document.domain.documentset.DocumentsetId;
import et.gov.ethgov.document.domain.documentset.DocumentsetRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateDocumentsetUseCase {
    private final DocumentsetRepository repository;
    private final Clock clock;

    public UpdateDocumentsetUseCase(DocumentsetRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public DocumentsetId execute(UpdateDocumentsetCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        DocumentsetId id = DocumentsetId.parse(command.documentsetId());
        Documentset aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("documentset not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateDocumentsetCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String documentsetId,
            String reason) {}
}
