package et.gov.ethgov.gis.application.gisexport;

import et.gov.ethgov.gis.domain.gisexport.Gisexport;
import et.gov.ethgov.gis.domain.gisexport.GisexportId;
import et.gov.ethgov.gis.domain.gisexport.GisexportRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivateGisexportUseCase {
    private final GisexportRepository repository;
    private final Clock clock;

    public ActivateGisexportUseCase(GisexportRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public GisexportId execute(ActivateGisexportCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        GisexportId id = GisexportId.parse(command.gisexportId());
        Gisexport aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("gisexport not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivateGisexportCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String gisexportId,
            String reason) {}
}
