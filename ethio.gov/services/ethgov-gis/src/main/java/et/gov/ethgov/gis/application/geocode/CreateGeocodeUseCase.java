package et.gov.ethgov.gis.application.geocode;

import et.gov.ethgov.gis.domain.geocode.Geocode;
import et.gov.ethgov.gis.domain.geocode.GeocodeId;
import et.gov.ethgov.gis.domain.geocode.GeocodeRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateGeocodeUseCase {
    private final GeocodeRepository repository;
    private final Clock clock;

    public CreateGeocodeUseCase(GeocodeRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public GeocodeId execute(CreateGeocodeCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Geocode aggregate = Geocode.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateGeocodeCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String geocodeId,
            String reason) {}
}
