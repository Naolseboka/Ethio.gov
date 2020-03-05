package et.gov.ethgov.directory.application.directoryentry;

import et.gov.ethgov.directory.domain.directoryentry.Directoryentry;
import et.gov.ethgov.directory.domain.directoryentry.DirectoryentryId;
import et.gov.ethgov.directory.domain.directoryentry.DirectoryentryRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivateDirectoryentryUseCase {
    private final DirectoryentryRepository repository;
    private final Clock clock;

    public ActivateDirectoryentryUseCase(DirectoryentryRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public DirectoryentryId execute(ActivateDirectoryentryCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        DirectoryentryId id = DirectoryentryId.parse(command.directoryentryId());
        Directoryentry aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("directoryentry not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivateDirectoryentryCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String directoryentryId,
            String reason) {}
}
