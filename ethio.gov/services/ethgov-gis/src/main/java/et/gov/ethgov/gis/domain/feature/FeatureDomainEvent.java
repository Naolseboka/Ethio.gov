package et.gov.ethgov.gis.domain.feature;

import java.time.Instant;

public sealed interface FeatureDomainEvent
        permits FeatureRegistered, FeatureUpdated, FeaturePublished, FeatureRetired, FeatureCancelled {
    FeatureId featureId();
    Instant occurredAt();
}
