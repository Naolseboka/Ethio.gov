package et.gov.ethgov.gis.application.spatialquery;

import et.gov.ethgov.gis.domain.spatialquery.Spatialquery;
import et.gov.ethgov.gis.domain.spatialquery.SpatialqueryId;
import et.gov.ethgov.gis.domain.spatialquery.SpatialqueryRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendSpatialqueryUseCase {
    private final SpatialqueryRepository repository;
    private final Clock clock;

    public SuspendSpatialqueryUseCase(SpatialqueryRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public SpatialqueryId execute(SuspendSpatialqueryCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        SpatialqueryId id = SpatialqueryId.parse(command.spatialqueryId());
        Spatialquery aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("spatialquery not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendSpatialqueryCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String spatialqueryId,
            String reason) {}
}
