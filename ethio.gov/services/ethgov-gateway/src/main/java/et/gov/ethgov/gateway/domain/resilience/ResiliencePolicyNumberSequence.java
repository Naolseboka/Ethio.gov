package et.gov.ethgov.gateway.domain.resilience;

import et.gov.ethgov.platform.commons.tenant.TenantId;

public interface ResiliencePolicyNumberSequence {
    long nextSequence(TenantId tenantId, int year);
}
