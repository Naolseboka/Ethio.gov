package et.gov.ethgov.directory.application.publiclisting;

import et.gov.ethgov.directory.domain.publiclisting.Publiclisting;
import et.gov.ethgov.directory.domain.publiclisting.PubliclistingId;
import et.gov.ethgov.directory.domain.publiclisting.PubliclistingRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendPubliclistingUseCase {
    private final PubliclistingRepository repository;
    private final Clock clock;

    public SuspendPubliclistingUseCase(PubliclistingRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public PubliclistingId execute(SuspendPubliclistingCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        PubliclistingId id = PubliclistingId.parse(command.publiclistingId());
        Publiclisting aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("publiclisting not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendPubliclistingCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String publiclistingId,
            String reason) {}
}
