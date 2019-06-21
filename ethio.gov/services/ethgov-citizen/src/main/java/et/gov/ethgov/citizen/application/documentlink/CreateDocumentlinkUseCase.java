package et.gov.ethgov.citizen.application.documentlink;

import et.gov.ethgov.citizen.domain.documentlink.Documentlink;
import et.gov.ethgov.citizen.domain.documentlink.DocumentlinkId;
import et.gov.ethgov.citizen.domain.documentlink.DocumentlinkRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateDocumentlinkUseCase {
    private final DocumentlinkRepository repository;
    private final Clock clock;

    public CreateDocumentlinkUseCase(DocumentlinkRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public DocumentlinkId execute(CreateDocumentlinkCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Documentlink aggregate = Documentlink.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateDocumentlinkCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String documentlinkId,
            String reason) {}
}
