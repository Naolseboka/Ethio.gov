package et.gov.ethgov.directory.application.openinghours;

import et.gov.ethgov.directory.domain.openinghours.Openinghours;
import et.gov.ethgov.directory.domain.openinghours.OpeninghoursId;
import et.gov.ethgov.directory.domain.openinghours.OpeninghoursRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendOpeninghoursUseCase {
    private final OpeninghoursRepository repository;
    private final Clock clock;

    public SuspendOpeninghoursUseCase(OpeninghoursRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public OpeninghoursId execute(SuspendOpeninghoursCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        OpeninghoursId id = OpeninghoursId.parse(command.openinghoursId());
        Openinghours aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("openinghours not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendOpeninghoursCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String openinghoursId,
            String reason) {}
}
