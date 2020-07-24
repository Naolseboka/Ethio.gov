package et.gov.ethgov.document.application.archivebox;

import et.gov.ethgov.document.domain.archivebox.Archivebox;
import et.gov.ethgov.document.domain.archivebox.ArchiveboxId;
import et.gov.ethgov.document.domain.archivebox.ArchiveboxRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateArchiveboxUseCase {
    private final ArchiveboxRepository repository;
    private final Clock clock;

    public CreateArchiveboxUseCase(ArchiveboxRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public ArchiveboxId execute(CreateArchiveboxCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Archivebox aggregate = Archivebox.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateArchiveboxCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String archiveboxId,
            String reason) {}
}
