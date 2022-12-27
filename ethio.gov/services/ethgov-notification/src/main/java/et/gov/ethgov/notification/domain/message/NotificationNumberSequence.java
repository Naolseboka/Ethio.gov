package et.gov.ethgov.notification.domain.message;

import et.gov.ethgov.platform.commons.tenant.TenantId;

public interface NotificationNumberSequence {
    long nextSequence(TenantId tenantId, int year);
}
