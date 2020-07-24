package et.gov.ethgov.document.application.archivebox;

import et.gov.ethgov.document.domain.archivebox.Archivebox;
import et.gov.ethgov.document.domain.archivebox.ArchiveboxId;
import et.gov.ethgov.document.domain.archivebox.ArchiveboxRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivateArchiveboxUseCase {
    private final ArchiveboxRepository repository;
    private final Clock clock;

    public ActivateArchiveboxUseCase(ArchiveboxRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public ArchiveboxId execute(ActivateArchiveboxCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        ArchiveboxId id = ArchiveboxId.parse(command.archiveboxId());
        Archivebox aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("archivebox not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivateArchiveboxCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String archiveboxId,
            String reason) {}
}
