package et.gov.ethgov.gis.application.heatmap;

import et.gov.ethgov.gis.domain.heatmap.Heatmap;
import et.gov.ethgov.gis.domain.heatmap.HeatmapId;
import et.gov.ethgov.gis.domain.heatmap.HeatmapRepository;
import java.util.Objects;

public final class GetHeatmapUseCase {
    private final HeatmapRepository repository;

    public GetHeatmapUseCase(HeatmapRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Heatmap execute(String rawId) {
        HeatmapId id = HeatmapId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("heatmap not found: " + rawId));
    }
}
