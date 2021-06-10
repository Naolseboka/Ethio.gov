package et.gov.ethgov.gis.application.zone;

import et.gov.ethgov.gis.domain.zone.Zone;
import et.gov.ethgov.gis.domain.zone.ZoneId;
import et.gov.ethgov.gis.domain.zone.ZoneRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendZoneUseCase {
    private final ZoneRepository repository;
    private final Clock clock;

    public SuspendZoneUseCase(ZoneRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public ZoneId execute(SuspendZoneCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        ZoneId id = ZoneId.parse(command.zoneId());
        Zone aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("zone not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendZoneCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String zoneId,
            String reason) {}
}
