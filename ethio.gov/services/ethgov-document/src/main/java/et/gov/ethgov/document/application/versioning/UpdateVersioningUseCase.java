package et.gov.ethgov.document.application.versioning;

import et.gov.ethgov.document.domain.versioning.Versioning;
import et.gov.ethgov.document.domain.versioning.VersioningId;
import et.gov.ethgov.document.domain.versioning.VersioningRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateVersioningUseCase {
    private final VersioningRepository repository;
    private final Clock clock;

    public UpdateVersioningUseCase(VersioningRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public VersioningId execute(UpdateVersioningCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        VersioningId id = VersioningId.parse(command.versioningId());
        Versioning aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("versioning not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateVersioningCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String versioningId,
            String reason) {}
}
