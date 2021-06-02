package et.gov.ethgov.gis.application.spatialquery;

import et.gov.ethgov.gis.domain.spatialquery.Spatialquery;
import et.gov.ethgov.gis.domain.spatialquery.SpatialqueryId;
import et.gov.ethgov.gis.domain.spatialquery.SpatialqueryRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateSpatialqueryUseCase {
    private final SpatialqueryRepository repository;
    private final Clock clock;

    public CreateSpatialqueryUseCase(SpatialqueryRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public SpatialqueryId execute(CreateSpatialqueryCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Spatialquery aggregate = Spatialquery.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateSpatialqueryCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String spatialqueryId,
            String reason) {}
}
