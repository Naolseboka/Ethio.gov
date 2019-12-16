package et.gov.ethgov.developer.domain.credential;

import et.gov.ethgov.platform.commons.tenant.TenantId;

public interface CredentialNumberSequence {
    long nextSequence(TenantId tenantId, int year);
}
