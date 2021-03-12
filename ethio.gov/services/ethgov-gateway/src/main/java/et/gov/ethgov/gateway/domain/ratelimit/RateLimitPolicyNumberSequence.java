package et.gov.ethgov.gateway.domain.ratelimit;

import et.gov.ethgov.platform.commons.tenant.TenantId;

public interface RateLimitPolicyNumberSequence {
    long nextSequence(TenantId tenantId, int year);
}
