package et.gov.ethgov.gis.domain.heatmap;

import java.util.List;
import java.util.Optional;

public interface HeatmapRepository {
    Heatmap save(Heatmap aggregate);

    Optional<Heatmap> findById(HeatmapId id);

    Optional<Heatmap> findByReferenceCode(String tenantId, String referenceCode);

    List<Heatmap> findByTenant(String tenantId, int limit);

    void delete(HeatmapId id);
}
