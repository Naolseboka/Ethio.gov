package et.gov.ethgov.gis.domain.feature;

import et.gov.ethgov.platform.commons.tenant.TenantId;

public interface FeatureNumberSequence {
    long nextSequence(TenantId tenantId, int year);
}
