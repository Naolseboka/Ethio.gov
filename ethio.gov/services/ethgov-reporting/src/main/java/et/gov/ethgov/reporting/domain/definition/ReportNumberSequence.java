package et.gov.ethgov.reporting.domain.definition;

import et.gov.ethgov.platform.commons.tenant.TenantId;

public interface ReportNumberSequence {
    long nextSequence(TenantId tenantId, int year);
}
