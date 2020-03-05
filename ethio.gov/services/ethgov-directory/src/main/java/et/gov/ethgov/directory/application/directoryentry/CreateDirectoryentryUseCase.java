package et.gov.ethgov.directory.application.directoryentry;

import et.gov.ethgov.directory.domain.directoryentry.Directoryentry;
import et.gov.ethgov.directory.domain.directoryentry.DirectoryentryId;
import et.gov.ethgov.directory.domain.directoryentry.DirectoryentryRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateDirectoryentryUseCase {
    private final DirectoryentryRepository repository;
    private final Clock clock;

    public CreateDirectoryentryUseCase(DirectoryentryRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public DirectoryentryId execute(CreateDirectoryentryCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Directoryentry aggregate = Directoryentry.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateDirectoryentryCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String directoryentryId,
            String reason) {}
}
