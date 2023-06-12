package et.gov.ethgov.opendata.domain.resource;

import et.gov.ethgov.platform.commons.tenant.TenantId;

public interface ResourceNumberSequence {
    long nextSequence(TenantId tenantId, int year);
}
