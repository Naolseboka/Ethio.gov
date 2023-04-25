package et.gov.ethgov.opendata.domain.dataset;

import et.gov.ethgov.platform.commons.tenant.TenantId;

public interface DatasetNumberSequence {
    long nextSequence(TenantId tenantId, int year);
}
