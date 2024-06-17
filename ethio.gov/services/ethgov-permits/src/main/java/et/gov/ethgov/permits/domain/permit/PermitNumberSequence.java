package et.gov.ethgov.permits.domain.permit;

import et.gov.ethgov.platform.commons.tenant.TenantId;

public interface PermitNumberSequence {
    long nextSequence(TenantId tenantId, int year);
}
