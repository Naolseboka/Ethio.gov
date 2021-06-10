package et.gov.ethgov.gis.application.zone;

import et.gov.ethgov.gis.domain.zone.Zone;
import et.gov.ethgov.gis.domain.zone.ZoneId;
import et.gov.ethgov.gis.domain.zone.ZoneRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateZoneUseCase {
    private final ZoneRepository repository;
    private final Clock clock;

    public CreateZoneUseCase(ZoneRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public ZoneId execute(CreateZoneCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Zone aggregate = Zone.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateZoneCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String zoneId,
            String reason) {}
}
