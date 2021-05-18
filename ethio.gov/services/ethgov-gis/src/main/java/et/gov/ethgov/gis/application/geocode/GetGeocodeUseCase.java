package et.gov.ethgov.gis.application.geocode;

import et.gov.ethgov.gis.domain.geocode.Geocode;
import et.gov.ethgov.gis.domain.geocode.GeocodeId;
import et.gov.ethgov.gis.domain.geocode.GeocodeRepository;
import java.util.Objects;

public final class GetGeocodeUseCase {
    private final GeocodeRepository repository;

    public GetGeocodeUseCase(GeocodeRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Geocode execute(String rawId) {
        GeocodeId id = GeocodeId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("geocode not found: " + rawId));
    }
}
