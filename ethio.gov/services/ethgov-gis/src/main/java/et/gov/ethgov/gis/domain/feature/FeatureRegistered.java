package et.gov.ethgov.gis.domain.feature;

import et.gov.ethgov.platform.commons.tenant.TenantId;
import java.time.Instant;

public record FeatureRegistered(
        FeatureId featureId, TenantId tenantId, FeatureNumber featureNumber, FeatureType type, Instant occurredAt)
        implements FeatureDomainEvent {}
