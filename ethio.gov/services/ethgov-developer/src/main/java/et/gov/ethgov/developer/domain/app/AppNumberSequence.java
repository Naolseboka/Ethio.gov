package et.gov.ethgov.developer.domain.app;

import et.gov.ethgov.platform.commons.tenant.TenantId;

public interface AppNumberSequence {
    long nextSequence(TenantId tenantId, int year);
}
