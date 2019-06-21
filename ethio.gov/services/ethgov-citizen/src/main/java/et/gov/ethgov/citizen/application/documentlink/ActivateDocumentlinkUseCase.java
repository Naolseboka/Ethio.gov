package et.gov.ethgov.citizen.application.documentlink;

import et.gov.ethgov.citizen.domain.documentlink.Documentlink;
import et.gov.ethgov.citizen.domain.documentlink.DocumentlinkId;
import et.gov.ethgov.citizen.domain.documentlink.DocumentlinkRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivateDocumentlinkUseCase {
    private final DocumentlinkRepository repository;
    private final Clock clock;

    public ActivateDocumentlinkUseCase(DocumentlinkRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public DocumentlinkId execute(ActivateDocumentlinkCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        DocumentlinkId id = DocumentlinkId.parse(command.documentlinkId());
        Documentlink aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("documentlink not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivateDocumentlinkCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String documentlinkId,
            String reason) {}
}
