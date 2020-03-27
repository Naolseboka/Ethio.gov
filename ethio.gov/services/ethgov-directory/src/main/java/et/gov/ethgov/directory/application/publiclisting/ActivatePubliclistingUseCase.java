package et.gov.ethgov.directory.application.publiclisting;

import et.gov.ethgov.directory.domain.publiclisting.Publiclisting;
import et.gov.ethgov.directory.domain.publiclisting.PubliclistingId;
import et.gov.ethgov.directory.domain.publiclisting.PubliclistingRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivatePubliclistingUseCase {
    private final PubliclistingRepository repository;
    private final Clock clock;

    public ActivatePubliclistingUseCase(PubliclistingRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public PubliclistingId execute(ActivatePubliclistingCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        PubliclistingId id = PubliclistingId.parse(command.publiclistingId());
        Publiclisting aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("publiclisting not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivatePubliclistingCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String publiclistingId,
            String reason) {}
}
