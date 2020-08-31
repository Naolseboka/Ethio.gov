package et.gov.ethgov.document.application.versioning;

import et.gov.ethgov.document.domain.versioning.Versioning;
import et.gov.ethgov.document.domain.versioning.VersioningId;
import et.gov.ethgov.document.domain.versioning.VersioningRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendVersioningUseCase {
    private final VersioningRepository repository;
    private final Clock clock;

    public SuspendVersioningUseCase(VersioningRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public VersioningId execute(SuspendVersioningCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        VersioningId id = VersioningId.parse(command.versioningId());
        Versioning aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("versioning not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendVersioningCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String versioningId,
            String reason) {}
}
