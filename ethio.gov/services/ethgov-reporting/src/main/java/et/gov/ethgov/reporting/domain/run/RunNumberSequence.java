package et.gov.ethgov.reporting.domain.run;

import et.gov.ethgov.platform.commons.tenant.TenantId;

public interface RunNumberSequence {
    long nextSequence(TenantId tenantId, int year);
}
