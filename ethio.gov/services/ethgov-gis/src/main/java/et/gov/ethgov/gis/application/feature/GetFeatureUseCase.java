package et.gov.ethgov.gis.application.feature;

import et.gov.ethgov.gis.domain.feature.Feature;
import et.gov.ethgov.gis.domain.feature.FeatureId;
import et.gov.ethgov.gis.domain.feature.FeatureRepository;
import java.util.Objects;

public final class GetFeatureUseCase {
    private final FeatureRepository repository;

    public GetFeatureUseCase(FeatureRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Feature execute(String rawId) {
        FeatureId id = FeatureId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("feature not found: " + rawId));
    }
}
