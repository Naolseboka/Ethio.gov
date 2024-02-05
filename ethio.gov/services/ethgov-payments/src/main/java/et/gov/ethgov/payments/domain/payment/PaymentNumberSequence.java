package et.gov.ethgov.payments.domain.payment;

import et.gov.ethgov.platform.commons.tenant.TenantId;

public interface PaymentNumberSequence {
    long nextSequence(TenantId tenantId, int year);
}
