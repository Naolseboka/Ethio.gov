package et.gov.ethgov.gis.application.basemap;

import et.gov.ethgov.gis.domain.basemap.Basemap;
import et.gov.ethgov.gis.domain.basemap.BasemapId;
import et.gov.ethgov.gis.domain.basemap.BasemapRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateBasemapUseCase {
    private final BasemapRepository repository;
    private final Clock clock;

    public CreateBasemapUseCase(BasemapRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public BasemapId execute(CreateBasemapCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Basemap aggregate = Basemap.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateBasemapCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String basemapId,
            String reason) {}
}
