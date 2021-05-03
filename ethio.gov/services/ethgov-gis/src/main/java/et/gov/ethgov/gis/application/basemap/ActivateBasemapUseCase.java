package et.gov.ethgov.gis.application.basemap;

import et.gov.ethgov.gis.domain.basemap.Basemap;
import et.gov.ethgov.gis.domain.basemap.BasemapId;
import et.gov.ethgov.gis.domain.basemap.BasemapRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivateBasemapUseCase {
    private final BasemapRepository repository;
    private final Clock clock;

    public ActivateBasemapUseCase(BasemapRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public BasemapId execute(ActivateBasemapCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        BasemapId id = BasemapId.parse(command.basemapId());
        Basemap aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("basemap not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivateBasemapCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String basemapId,
            String reason) {}
}
