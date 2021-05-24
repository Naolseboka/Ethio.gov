package et.gov.ethgov.gis.application.heatmap;

import et.gov.ethgov.gis.domain.heatmap.Heatmap;
import et.gov.ethgov.gis.domain.heatmap.HeatmapId;
import et.gov.ethgov.gis.domain.heatmap.HeatmapRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateHeatmapUseCase {
    private final HeatmapRepository repository;
    private final Clock clock;

    public CreateHeatmapUseCase(HeatmapRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public HeatmapId execute(CreateHeatmapCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Heatmap aggregate = Heatmap.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateHeatmapCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String heatmapId,
            String reason) {}
}
