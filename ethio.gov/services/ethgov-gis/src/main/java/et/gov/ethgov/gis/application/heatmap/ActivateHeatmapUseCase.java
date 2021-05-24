package et.gov.ethgov.gis.application.heatmap;

import et.gov.ethgov.gis.domain.heatmap.Heatmap;
import et.gov.ethgov.gis.domain.heatmap.HeatmapId;
import et.gov.ethgov.gis.domain.heatmap.HeatmapRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivateHeatmapUseCase {
    private final HeatmapRepository repository;
    private final Clock clock;

    public ActivateHeatmapUseCase(HeatmapRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public HeatmapId execute(ActivateHeatmapCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        HeatmapId id = HeatmapId.parse(command.heatmapId());
        Heatmap aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("heatmap not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivateHeatmapCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String heatmapId,
            String reason) {}
}
