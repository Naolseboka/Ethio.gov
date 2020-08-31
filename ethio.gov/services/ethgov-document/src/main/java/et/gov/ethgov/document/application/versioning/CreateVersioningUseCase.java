package et.gov.ethgov.document.application.versioning;

import et.gov.ethgov.document.domain.versioning.Versioning;
import et.gov.ethgov.document.domain.versioning.VersioningId;
import et.gov.ethgov.document.domain.versioning.VersioningRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateVersioningUseCase {
    private final VersioningRepository repository;
    private final Clock clock;

    public CreateVersioningUseCase(VersioningRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public VersioningId execute(CreateVersioningCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Versioning aggregate = Versioning.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateVersioningCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String versioningId,
            String reason) {}
}
