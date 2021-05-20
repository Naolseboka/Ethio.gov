package et.gov.ethgov.gis.application.gisexport;

import et.gov.ethgov.gis.domain.gisexport.Gisexport;
import et.gov.ethgov.gis.domain.gisexport.GisexportId;
import et.gov.ethgov.gis.domain.gisexport.GisexportRepository;
import java.util.Objects;

public final class GetGisexportUseCase {
    private final GisexportRepository repository;

    public GetGisexportUseCase(GisexportRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Gisexport execute(String rawId) {
        GisexportId id = GisexportId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("gisexport not found: " + rawId));
    }
}
