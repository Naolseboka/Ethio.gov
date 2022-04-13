package et.gov.ethgov.land.domain.parcel;

import et.gov.ethgov.platform.commons.tenant.TenantId;

public interface ParcelNumberSequence {
    long nextSequence(TenantId tenantId, int year);
}
