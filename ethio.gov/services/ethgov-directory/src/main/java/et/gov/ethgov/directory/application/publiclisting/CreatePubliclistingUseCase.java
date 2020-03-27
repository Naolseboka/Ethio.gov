package et.gov.ethgov.directory.application.publiclisting;

import et.gov.ethgov.directory.domain.publiclisting.Publiclisting;
import et.gov.ethgov.directory.domain.publiclisting.PubliclistingId;
import et.gov.ethgov.directory.domain.publiclisting.PubliclistingRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreatePubliclistingUseCase {
    private final PubliclistingRepository repository;
    private final Clock clock;

    public CreatePubliclistingUseCase(PubliclistingRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public PubliclistingId execute(CreatePubliclistingCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Publiclisting aggregate = Publiclisting.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreatePubliclistingCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String publiclistingId,
            String reason) {}
}
