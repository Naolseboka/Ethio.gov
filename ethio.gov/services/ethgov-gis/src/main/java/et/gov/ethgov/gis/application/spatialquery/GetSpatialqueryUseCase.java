package et.gov.ethgov.gis.application.spatialquery;

import et.gov.ethgov.gis.domain.spatialquery.Spatialquery;
import et.gov.ethgov.gis.domain.spatialquery.SpatialqueryId;
import et.gov.ethgov.gis.domain.spatialquery.SpatialqueryRepository;
import java.util.Objects;

public final class GetSpatialqueryUseCase {
    private final SpatialqueryRepository repository;

    public GetSpatialqueryUseCase(SpatialqueryRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Spatialquery execute(String rawId) {
        SpatialqueryId id = SpatialqueryId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("spatialquery not found: " + rawId));
    }
}
