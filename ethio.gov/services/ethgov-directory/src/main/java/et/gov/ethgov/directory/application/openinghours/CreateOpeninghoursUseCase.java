package et.gov.ethgov.directory.application.openinghours;

import et.gov.ethgov.directory.domain.openinghours.Openinghours;
import et.gov.ethgov.directory.domain.openinghours.OpeninghoursId;
import et.gov.ethgov.directory.domain.openinghours.OpeninghoursRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateOpeninghoursUseCase {
    private final OpeninghoursRepository repository;
    private final Clock clock;

    public CreateOpeninghoursUseCase(OpeninghoursRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public OpeninghoursId execute(CreateOpeninghoursCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Openinghours aggregate = Openinghours.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateOpeninghoursCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String openinghoursId,
            String reason) {}
}
