package et.gov.ethgov.gis.application.heatmap;

import et.gov.ethgov.gis.domain.heatmap.Heatmap;
import et.gov.ethgov.gis.domain.heatmap.HeatmapRepository;
import java.util.List;
import java.util.Objects;

public final class ListHeatmapUseCase {
    private final HeatmapRepository repository;

    public ListHeatmapUseCase(HeatmapRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Heatmap> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
