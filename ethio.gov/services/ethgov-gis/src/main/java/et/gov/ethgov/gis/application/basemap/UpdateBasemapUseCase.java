package et.gov.ethgov.gis.application.basemap;

import et.gov.ethgov.gis.domain.basemap.Basemap;
import et.gov.ethgov.gis.domain.basemap.BasemapId;
import et.gov.ethgov.gis.domain.basemap.BasemapRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateBasemapUseCase {
    private final BasemapRepository repository;
    private final Clock clock;

    public UpdateBasemapUseCase(BasemapRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public BasemapId execute(UpdateBasemapCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        BasemapId id = BasemapId.parse(command.basemapId());
        Basemap aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("basemap not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateBasemapCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String basemapId,
            String reason) {}
}
