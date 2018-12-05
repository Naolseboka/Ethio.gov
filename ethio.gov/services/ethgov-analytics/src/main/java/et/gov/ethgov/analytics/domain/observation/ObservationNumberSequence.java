package et.gov.ethgov.analytics.domain.observation;

import et.gov.ethgov.platform.commons.tenant.TenantId;

public interface ObservationNumberSequence {
    long nextSequence(TenantId tenantId, int year);
}
