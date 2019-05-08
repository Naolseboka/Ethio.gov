package et.gov.ethgov.casemgmt.domain.casefile;

import et.gov.ethgov.platform.commons.tenant.TenantId;

public interface CaseNumberSequence {

    long nextSequence(TenantId tenantId, int year);
}
