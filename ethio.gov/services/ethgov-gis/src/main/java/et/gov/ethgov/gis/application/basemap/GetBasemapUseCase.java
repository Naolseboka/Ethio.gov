package et.gov.ethgov.gis.application.basemap;

import et.gov.ethgov.gis.domain.basemap.Basemap;
import et.gov.ethgov.gis.domain.basemap.BasemapId;
import et.gov.ethgov.gis.domain.basemap.BasemapRepository;
import java.util.Objects;

public final class GetBasemapUseCase {
    private final BasemapRepository repository;

    public GetBasemapUseCase(BasemapRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Basemap execute(String rawId) {
        BasemapId id = BasemapId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("basemap not found: " + rawId));
    }
}
