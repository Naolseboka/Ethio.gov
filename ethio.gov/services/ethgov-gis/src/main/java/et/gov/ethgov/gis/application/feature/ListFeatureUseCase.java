package et.gov.ethgov.gis.application.feature;

import et.gov.ethgov.gis.domain.feature.Feature;
import et.gov.ethgov.gis.domain.feature.FeatureRepository;
import java.util.List;
import java.util.Objects;

public final class ListFeatureUseCase {
    private final FeatureRepository repository;

    public ListFeatureUseCase(FeatureRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Feature> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
