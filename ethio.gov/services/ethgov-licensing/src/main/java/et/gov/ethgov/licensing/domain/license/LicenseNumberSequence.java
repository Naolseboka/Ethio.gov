package et.gov.ethgov.licensing.domain.license;

import et.gov.ethgov.platform.commons.tenant.TenantId;

public interface LicenseNumberSequence {
    long nextSequence(TenantId tenantId, int year);
}
