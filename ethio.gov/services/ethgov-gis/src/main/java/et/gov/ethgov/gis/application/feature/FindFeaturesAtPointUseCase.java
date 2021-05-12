package et.gov.ethgov.gis.application.feature;

import et.gov.ethgov.gis.domain.feature.FeatureId;
import et.gov.ethgov.gis.domain.feature.FeatureRepository;
import et.gov.ethgov.gis.domain.feature.GisFeature;
import et.gov.ethgov.platform.commons.tenant.TenantId;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FindFeaturesAtPointUseCase {

    private final FeatureRepository features;

    public FindFeaturesAtPointUseCase(FeatureRepository features) {
        this.features = features;
    }

    @Transactional(readOnly = true)
    public List<GisFeature> execute(String tenantId, double longitude, double latitude) {
        Objects.requireNonNull(tenantId, "tenantId");
        if (longitude < -180 || longitude > 180 || latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException("longitude/latitude out of range");
        }
        List<FeatureId> ids =
                features.findPublishedContainingPoint(TenantId.of(tenantId), longitude, latitude);
        return ids.stream()
                .map(id -> features.findById(id).orElseThrow())
                .toList();
    }
}
