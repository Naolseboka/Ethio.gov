package et.gov.ethgov.gis.application.geocode;

import et.gov.ethgov.gis.domain.geocode.Geocode;
import et.gov.ethgov.gis.domain.geocode.GeocodeId;
import et.gov.ethgov.gis.domain.geocode.GeocodeRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendGeocodeUseCase {
    private final GeocodeRepository repository;
    private final Clock clock;

    public SuspendGeocodeUseCase(GeocodeRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public GeocodeId execute(SuspendGeocodeCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        GeocodeId id = GeocodeId.parse(command.geocodeId());
        Geocode aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("geocode not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendGeocodeCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String geocodeId,
            String reason) {}
}
