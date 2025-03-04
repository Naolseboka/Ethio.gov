package et.gov.ethgov.revenue.domain.bill;

import et.gov.ethgov.platform.commons.tenant.TenantId;

public interface BillNumberSequence {
    long nextSequence(TenantId tenantId, int year);
}
