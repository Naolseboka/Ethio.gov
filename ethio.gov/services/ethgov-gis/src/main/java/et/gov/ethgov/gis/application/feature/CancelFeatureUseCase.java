package et.gov.ethgov.gis.application.feature;

import et.gov.ethgov.gis.application.GisEventPublisher;
import et.gov.ethgov.gis.domain.feature.FeatureId;
import et.gov.ethgov.gis.domain.feature.FeatureRepository;
import et.gov.ethgov.gis.domain.feature.GisFeature;
import et.gov.ethgov.platform.commons.error.DomainException;
import java.time.Clock;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CancelFeatureUseCase {

    private final FeatureRepository features;
    private final GisEventPublisher eventPublisher;
    private final Clock clock;

    public CancelFeatureUseCase(FeatureRepository features, GisEventPublisher eventPublisher, Clock clock) {
        this.features = features;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
    }

    @Transactional
    public void execute(String featureId, String reason) {
        Objects.requireNonNull(featureId, "featureId");
        GisFeature feature = features.findById(FeatureId.of(featureId))
                .orElseThrow(() -> new DomainException(
                        "FEATURE_NOT_FOUND", "Feature Not Found", "Feature does not exist", 404));
        feature.cancel(reason, clock.instant());
        features.save(feature);
        eventPublisher.publishAll(feature.pullDomainEvents());
    }
}
