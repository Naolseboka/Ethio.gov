package et.gov.ethgov.search.domain.document;

import et.gov.ethgov.platform.commons.tenant.TenantId;

public interface DocumentNumberSequence {
    long nextSequence(TenantId tenantId, int year);
}
